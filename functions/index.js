const { onDocumentCreated, onDocumentUpdated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

// ─── CLOUD FUNCTION: Notificació quan es crea una multa ───────────────────────
exports.onFineCreated = onDocumentCreated("fines/{fineId}", async (event) => {
    const fine = event.data.data();
    if (!fine) return null;

    const userDoc = await admin.firestore()
        .collection("users")
        .doc(fine.userId)
        .get();

    if (!userDoc.exists) return null;

    const fcmToken = userDoc.data().fcmToken;
    if (!fcmToken) {
        console.log("Usuari sense token FCM:", fine.userId);
        return null;
    }

    const message = {
        token: fcmToken,
        notification: {
            title: "🚨 Nova multa!",
            body: `T'han posat una multa de ${fine.amount}€ per: ${fine.reason}`
        },
        data: {
            fineId: event.params.fineId,
            type: "new_fine"
        }
    };

    try {
        await admin.messaging().send(message);
        console.log("Notificació enviada a:", fine.userName);
    } catch (error) {
        console.error("Error enviant notificació:", error);
    }

    return null;
});

// ─── CLOUD FUNCTION: Notificació quan una multa es marca com a pagada ─────────
exports.onFinePaid = onDocumentUpdated("fines/{fineId}", async (event) => {
    const before = event.data.before.data();
    const after = event.data.after.data();

    if (before.status === after.status) return null;
    if (after.status !== "PAID") return null;

    const userDoc = await admin.firestore()
        .collection("users")
        .doc(after.userId)
        .get();

    if (!userDoc.exists) return null;

    const fcmToken = userDoc.data().fcmToken;
    if (!fcmToken) {
        console.log("Usuari sense token FCM:", after.userId);
        return null;
    }

    const message = {
        token: fcmToken,
        notification: {
            title: "✅ Multa pagada",
            body: `La teva multa de ${after.amount}€ ha estat confirmada com a pagada`
        },
        data: {
            fineId: event.params.fineId,
            type: "fine_paid"
        }
    };

    try {
        await admin.messaging().send(message);
        console.log("Notificació de pagament enviada a:", after.userName);
    } catch (error) {
        console.error("Error enviant notificació:", error);
    }

    return null;
});