const { onDocumentCreated, onDocumentUpdated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

// ─── CLOUD FUNCTION: Notificació quan es crea una multa ───────────────────────
exports.onFineCreated = onDocumentCreated(
    "fines/{fineId}",
    async (event) => {
        const fine = event.data.data();
        if (!fine) return null;
        if (!fine.teamId) {
            console.log("La multa no té teamId");
            return null;
        }
        const message = {
            topic: `team_${fine.teamId}`,
            notification: {
                title: "🚨 Nova multa!",
                body:
                    `${fine.userName} ha estat multat amb ${fine.amount}€ per: ${fine.reason}`
            },
            data: {
                fineId: event.params.fineId,
                type: "new_fine"
            }
        };
        try {
            await admin.messaging().send(message);
            console.log(
                `Notificació enviada al team_${fine.teamId}`
            );
        } catch (error) {
            console.error(
                "Error enviant notificació:",
                error
            );
        }
        return null;
    }
);

// ─── CLOUD FUNCTION: Notificació quan una multa es marca com a pagada ─────────
exports.onFinePaid = onDocumentUpdated(
    "fines/{fineId}",
    async (event) => {
        const before = event.data.before.data();
        const after = event.data.after.data();
        if (before.status === after.status) return null;
        if (after.status !== "PAID") return null;
        if (!after.teamId) {
            console.log("La multa no té teamId");
            return null;
        }
        const message = {
            topic: `team_${after.teamId}`,
            notification: {
                title: "✅ Multa pagada",
                body:
                    `${after.userName} ha pagat una multa de ${after.amount}€`
            },
            data: {
                fineId: event.params.fineId,
                type: "fine_paid"
            }
        };
        try {
            await admin.messaging().send(message);
            console.log(
                `Notificació de pagament enviada al team_${after.teamId}`
            );
        } catch (error) {
            console.error(
                "Error enviant notificació:",
                error
            );
        }
        return null;
    }
);