const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

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

test("Unauthenticated user: cannot read lab_attempts", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("lab_attempts").get());
});

test("Authenticated user: cannot read another user's lab_attempt", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("lab_attempts").doc("bob_att").set({
      userId: BOB_UID,
      studentName: "Bob",
      candidateNumber: "4250101002",
      experimentId: "mech_pendulum",
      experimentTitle: "Simple Pendulum",
      category: "Mechanics",
      examBoard: "WAEC",
      score: 85,
      maxScore: 100,
      slopeCalculated: 4.02,
      errorPercentage: 1.8,
      aiFeedback: "Good graph.",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("lab_attempts").doc("bob_att").get());
});

test("Authenticated user: can create and query their own lab_attempts with userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("lab_attempts").doc("alice_att_1").set({
      userId: ALICE_UID,
      studentName: "Chinedu Okafor",
      candidateNumber: "4250101001",
      experimentId: "mech_pendulum",
      experimentTitle: "Simple Pendulum (Determination of g)",
      category: "Mechanics",
      examBoard: "WAEC",
      score: 92,
      maxScore: 100,
      slopeCalculated: 4.01,
      errorPercentage: 1.2,
      aiFeedback: "Excellent line of best fit and precautions.",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("lab_attempts").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: fails query without userId filter (Query Enforcer)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("lab_attempts").get());
});

test("Shadow Update Test: rejects ghost fields on lab_attempts update", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("lab_attempts").doc("alice_att_2").set({
      userId: ALICE_UID,
      studentName: "Chinedu Okafor",
      candidateNumber: "4250101001",
      experimentId: "mech_pendulum",
      experimentTitle: "Simple Pendulum",
      category: "Mechanics",
      examBoard: "WAEC",
      score: 80,
      maxScore: 100,
      slopeCalculated: 4.0,
      errorPercentage: 2.0,
      aiFeedback: "Initial feedback",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("lab_attempts").doc("alice_att_2").update({
      score: 95,
      ghostField: true,
      updatedAt: new Date(),
    })
  );
});

test("ClassAssignment: owner can create and update active assignment, but cannot update once closed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("class_assignments").doc("ass_1").set({
      userId: ALICE_UID,
      teacherName: "Mrs. Adebayo",
      classLevel: "SS3",
      experimentId: "elec_potentiometer",
      experimentTitle: "Potentiometer: Comparison of EMFs",
      examBoard: "NECO",
      dueDate: "2026-11-30",
      targetAccuracyPercent: 5,
      status: "active",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("class_assignments").doc("ass_1").update({
      status: "closed",
      updatedAt: new Date(),
    })
  );

  // Terminal state lock: cannot update after closed
  await assertFails(
    aliceDb.collection("class_assignments").doc("ass_1").update({
      targetAccuracyPercent: 10,
      updatedAt: new Date(),
    })
  );
});
