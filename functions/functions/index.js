const functions = require("firebase-functions");

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

var db = admin.firestore();

exports.createUser = functions.https.onCall(async(data, context) => {
    const userInfo = {
        userName: data.userName,
        email: data.email,
        dateJoined: admin.firestore.Timestamp().fromMillis(data.dateJoined)
    };

    return await db.collection('users').doc(context.auth.uid).set(userInfo)
    .then(async() => {
        return {
            error: false
        };
    })
    .catch(err => {
        return {
            error: true,
            message: err
        };
    });
});

exports.updateUser = functions.https.onCall(async(data, context) => {
    const updateInfo = {
        name: data.name,
        gender: data.gender,
        height: data.height,
        heightUnit: data.heightUnit,
        weight: data.weight,
        weightUnit: data.weightUnit
    };

    return await db.collection('users').doc(context.auth.uid).update(updateInfo)
    .then(async() => {
        return {
            error: false
        };
    })
    .catch(err => {
        return {
            error: true,
            message: err
        };
    });
});