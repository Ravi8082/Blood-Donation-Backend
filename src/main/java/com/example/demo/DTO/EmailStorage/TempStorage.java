package com.example.demo.DTO.EmailStorage;

import com.example.demo.DTO.RegistrationUser.RegisterDto;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TempStorage {
    private static final Map<String, OTPData> emailOtpStorage = new ConcurrentHashMap<>();
    private static final Map<String, RegisterDto> registrationDataStorage = new ConcurrentHashMap<>();
    private static final long OTP_VALID_DURATION = 2 * 60 * 1000; // 2 minutes in milliseconds

    // ✅ Store OTP with timestamp
    public static void storeEmailOtp(String email, int otp) {
        emailOtpStorage.put(email, new OTPData(otp, System.currentTimeMillis()));
    }

    // ✅ Check if email exists in storage (for Resend OTP logic)
    public static boolean hasEmail(String email) {
        if (!emailOtpStorage.containsKey(email)) {
            return false;
        }
        if (isOtpExpired(email)) {
            emailOtpStorage.remove(email); // Remove expired OTP
            return false;
        }
        return true;
    }


    // ✅ Get OTP if valid
    public static Integer getEmailOtp(String email) {
        if (isOtpExpired(email)) {
            emailOtpStorage.remove(email); // Remove expired OTP
            return null;
        }
        return emailOtpStorage.get(email).getOtp();
    }


    // ✅ Remove OTP (on successful verification)
    public static void clearEmailOtp(String email) {
        emailOtpStorage.remove(email);
    }

    // ✅ Store Registration Data
    public static void storeRegistrationDataByEmail(String email, RegisterDto data) {
        registrationDataStorage.put(email, data);
    }

    public static RegisterDto getRegistrationDataByEmail(String email) {
        return registrationDataStorage.get(email);
    }

    public static void clearRegistrationDataByEmail(String email) {
        registrationDataStorage.remove(email);
    }

    // ✅ Check OTP expiry
    private static boolean isOtpExpired(String email) {
        OTPData otpData = emailOtpStorage.get(email);
        return otpData == null || System.currentTimeMillis() - otpData.getTimestamp() > OTP_VALID_DURATION;
    }

    // ✅ Inner class for OTP storage with timestamp
    private static class OTPData {
        private final int otp;
        private final long timestamp;

        public OTPData(int otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public int getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
