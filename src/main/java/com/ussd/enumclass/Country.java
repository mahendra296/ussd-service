package com.ussd.enumclass;

public enum Country {
    BOTSWANA("+267", "bw", 8 + 3, "Botswana"),
    NIGERIA("+234", "ng", 10 + 3, "Nigeria"),
    NAMIBIA("+264", "na", 9 + 3, "Namibia"),
    GHANA("+233", "gh", 10 + 3, "Ghana"),
    UGANDA("+256", "ug", 9 + 3, "Uganda"),
    MOZAMBIQUE("+258", "mz", 10 + 3, "Mozambique"),
    LESOTHO("+266", "ls", 8 + 3, "Lesotho"),
    ESWATINI("+268", "sz", 8 + 3, "Eswatini"),
    KENYA("+254", "ke", 9 + 3, "Kenya"),
    RWANDA("+250", "rw", 10 + 3, "Rwanda"),
    TANZANIA("+255", "tz", 10 + 3, "Tanzania");

    private final String phoneCode;
    private final String twoLetterISO;
    private final int phoneLength;
    private final String cap;

    Country(String phoneCode, String twoLetterISO, int phoneLength, String cap) {
        this.phoneCode = phoneCode;
        this.twoLetterISO = twoLetterISO;
        this.phoneLength = phoneLength;
        this.cap = cap;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public String getTwoLetterISO() {
        return twoLetterISO;
    }

    public int getPhoneLength() {
        return phoneLength;
    }

    public String getCap() {
        return cap;
    }

    public static Country fromPhoneCode(String phoneCode) {
        for (Country country : values()) {
            if (country.phoneCode.equals(phoneCode)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Invalid phoneCode: " + phoneCode);
    }

    public static Country fromTwoLetterISO(String twoLetterISO) {
        for (Country country : values()) {
            if (country.twoLetterISO.equals(twoLetterISO)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Invalid twoLetterISO: " + twoLetterISO);
    }

    public static Country fromPhoneNumber(String phoneNumber) {
        for (Country country : values()) {
            if (phoneNumber.startsWith(country.phoneCode)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Invalid phoneNumber: " + phoneNumber);
    }
}

