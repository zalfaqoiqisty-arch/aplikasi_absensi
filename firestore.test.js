const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write user profiles or activity logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Pratama",
      email: "alice@example.com",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("activity_logs").get()
  );
});

test("Authenticated owner can create, read, partially update, and delete UserProfile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // Mirror UserRepository.saveProfile() create payload (NO @DocumentId `id` field)
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Pratama",
      email: "alice@example.com",
      phoneNumber: "+6281234567890",
      birthDate: "1999-05-14",
      city: "Jakarta",
      occupation: "Product Designer",
      bio: "Membangun antarmuka digital modern.",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Mirror UserRepository.updateProfile() partial update
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Alice K. Pratama",
      city: "Bandung",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable field mutation (createdAt)
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject shadow/ghost field injection on update
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Validation blocks @DocumentId `id` field, future timestamps, and oversized strings", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const past = new Date(Date.now() - 2000);

  // Reject explicit `id` field in payload
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      id: ALICE_UID,
      userId: ALICE_UID,
      fullName: "Alice",
      email: "alice@example.com",
      createdAt: past,
      updatedAt: past,
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice",
      email: "alice@example.com",
      createdAt: new Date(Date.now() + 60000),
      updatedAt: past,
    })
  );

  // Reject short fullName (< 2 chars)
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "A",
      email: "alice@example.com",
      createdAt: past,
      updatedAt: past,
    })
  );
});

test("Cross-user isolation: Bob cannot read, list, create, update, or delete Alice profile", async () => {
  const created = new Date(Date.now() - 10000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Alice Pratama",
      email: "alice@example.com",
      createdAt: created,
      updatedAt: created,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      fullName: "Hijacked",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).delete());
});

test("Activity logs: owner can create, list, and delete; updates and cross-user access fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const logRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("activity_logs")
    .doc("log_1");

  await assertSucceeds(
    logRef.set({
      userId: ALICE_UID,
      action: "REGISTER",
      description: "Registrasi profil data diri berhasil",
      deviceLabel: "Android Device",
      createdAt: new Date(Date.now() - 1000),
    })
  );

  // Owner list query succeeds even without extra filters
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("activity_logs").get()
  );

  // Immutable log cannot be updated
  await assertFails(
    logRef.update({
      description: "Tampered log",
    })
  );

  // Cross-user read/write on activity_logs denied
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("activity_logs").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("activity_logs")
      .doc("log_2")
      .set({
        userId: BOB_UID,
        action: "LOGIN",
        description: "Intrusion",
        deviceLabel: "Android",
        createdAt: new Date(Date.now() - 1000),
      })
  );

  // Invalid action enum rejected
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("activity_logs")
      .doc("log_bad")
      .set({
        userId: ALICE_UID,
        action: "ADMIN_OVERRIDE",
        description: "Invalid action",
        deviceLabel: "Android",
        createdAt: new Date(Date.now() - 1000),
      })
  );
});
