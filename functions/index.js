const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { getFirestore, FieldValue } = require('firebase-admin/firestore');
const nodemailer = require("nodemailer"); // <--- Import Nodemailer

// IMPORT SECRETS
// We use a try-catch so the app doesn't crash if someone else clones it without the file
let secrets;
try {
    secrets = require("./secrets");
} catch (e) {
    // Fallback for public repo (empty strings)
    secrets = { email: "", password: "" };
    console.warn("secrets.js file is missing. Email sending will fail.");
}

if (admin.apps.length === 0) {
  admin.initializeApp();
}

// 1. Configure the Email Transporter (The "Postman")
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: secrets.email,       // <--- PUT YOUR EMAIL HERE
    pass: secrets.password  // <--- PUT THE APP PASSWORD HERE
  }
});

// 1. Generate and "Send" OTP (Console Log only)
exports.sendEmailOTP = functions.https.onCall(async (data, context) => {
//  const email = data.email;

// --- DEBUGGING START ---
  // 1. FIX: Do NOT stringify the whole 'data' object (it causes a crash).
  // Instead, we just safely extract what we need.
  // Handle Gen 1 vs Gen 2 difference automatically
  // If 'data' has a 'data' property, it's likely a Gen 2 request object
  const actualData = data.data ? data.data : data;
  const email = actualData.email;

  console.log("EXTRACTED EMAIL:", email);
  // --- DEBUGGING END ---

  // 1. Safety Check: Stop the crash if email is missing
  if (!email || typeof email !== 'string') {
    throw new functions.https.HttpsError(
      'invalid-argument',
      'The function must be called with an "email" argument.'
    );
  }

  const otp = Math.floor(100000 + Math.random() * 900000).toString();

  // Save to Firestore so we can verify it later
  // Note: We use the 'otp_codes' collection
  await admin.firestore().collection('otp_codes').doc(email).set({
    otp: otp,
    timestamp: FieldValue.serverTimestamp()
  });

  // --- EMULATOR TRICK ---
  // Instead of emailing, we just print it here.
  // Look at your terminal to see this code!
//  console.log(`\n\n [EMULATOR] OTP for ${email} is: ${otp} \n\n`);

  // 4. SEND REAL EMAIL
    const mailOptions = {
      from: 'My App <noreply@myapp.com>', // The "From" name
      to: email,                          // The user's email
      subject: 'Your Login Code',
      text: `Your verification code is: ${otp}`
    };

    try {
      await transporter.sendMail(mailOptions);
      console.log(`Email sent successfully to ${email}`);
    } catch (error) {
      console.error("Error sending email:", error);
      // Don't crash the app if email fails, but log it clearly
      throw new functions.https.HttpsError('internal', 'Failed to send email.');
    }

  return { success: true };
});

// 2. Verify OTP
exports.verifyEmailOTP = functions.https.onCall(async (data, context) => {
  const email = data.data.email;
  const userCode = data.data.code;
    console.log("Hello");
  // Check Firestore
  const doc = await admin.firestore().collection('otp_codes').doc(email).get();

  if (!doc.exists) {
    throw new functions.https.HttpsError('not-found', 'Email not found or expired');
  }

  const serverOtp = doc.data().otp;

  if (serverOtp === userCode) {
    // Correct OTP!

    // 1. Delete the used OTP
    await admin.firestore().collection('otp_codes').doc(email).delete();

    // 2. Find or Create User
    let uid;
    try {
      const user = await admin.auth().getUserByEmail(email);
      uid = user.uid;
    } catch (e) {
      // User doesn't exist, create new one
      const newUser = await admin.auth().createUser({ email: email });
      uid = newUser.uid;
    }

    // 3. Create Custom Token
    const token = await admin.auth().createCustomToken(uid);

    return { token: token };

  } else {
    throw new functions.https.HttpsError('invalid-argument', 'Incorrect Code');
  }
});