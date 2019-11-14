package com.dbvertex.company.h1bq.Util;

public class UIValidation {

    public static final String SUCCESS = "success";
    public static final String BLANK_MSG = "Blank does not allow!";
    public static final String NAME_LENGTH_MSG = "Length must be  3-50!";
    public static final String ADDRESS_LENGTH_MSG = "Length must be  5-100!";
    public static final String PASSWORD_LENGTH_MSG = "Length must be  6-12!";
    public static final String CPASSWORD_MSG = "Password does not match!";
    public static final String MOBILE_LENGTH_MSG = "Invalid mobile no.";
    public static final String NUMBER_MSG = "Only number is allowed!";
    public static final String EMAIL_MSG = "Invalid Email Format!";
    public static final String ZIPCODE_MSG = "Invalid ZIP Code!";
    public static final String CEMAIL_MSG = "Email Id does not match!";
    public static final String ZIP_MSG = "Please enter a valid Zip code! ";
    public static final String ZIP_LENGTH_MSG = "Please enter a valid Zip code! ";






    // Name
    public static String nameValidate(String name, boolean isBlankCheck,String message)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (name == null || name.isEmpty()) {
                msg = message;
                return msg;
            }
        }
//        int length = name.length();
//                if(length<3 || length>50)
//                {
//                    msg = NAME_LENGTH_MSG;
//                }
        return msg;
    }

    // Address
    public static String addressValidate(String address, boolean isBlankCheck,String messge)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (address == null || address.isEmpty()) {
                msg = messge;
                return msg;
            }
        }
        int length = address.length();
        if(length<5 || length>100)
        {
            msg = ADDRESS_LENGTH_MSG;
        }
        return msg;
    }

    // Mobile
    public static String mobileValidate(String mobile, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (mobile == null || mobile.isEmpty()) {
                msg = "Mobile No is required";
                return msg;
            }
        }
//        if (!mobile.matches("[0-9]+")) {
//            msg = NUMBER_MSG;;
//        }

            int length = mobile.length();
            if (length < 8 || length >14) {
                msg = MOBILE_LENGTH_MSG;

        }
        return msg;
    }

    // Number
    public static String numberValidate(String number, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (number == null || number.isEmpty()) {
                msg = BLANK_MSG;
                return msg;
            }
        }

        if (!number.matches("[0-9]+")) {
            msg = NUMBER_MSG;;
        }

        return msg;
    }

    // Number
    public static String emailValidate(String email, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (email == null || email.isEmpty()) {
                msg = "Please enter a valid email id";
                return msg;
            }
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(emailPattern)) {
            msg = "Please enter a valid email id";;
        }

        return msg;
    }

    public static String zipValidate(String zip, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (zip == null || zip.isEmpty()) {
                msg = "Zip code is required";
                return msg;
            }
        }

        int length = zip.length();
        if (length < 4 || length >10) {
            msg = ZIP_LENGTH_MSG;

        }
        return msg;
    }
    // Password
    public static String passwordValidate(String password, boolean isBlankCheck,String from)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (password == null || password.isEmpty()) {
                if (from.equalsIgnoreCase("login"))
                {
                    msg = "Password is required";
                }
                else if (from.equalsIgnoreCase("changenewpass"))
                {
                    msg = "New Password is required";
                }
                else if (from.equalsIgnoreCase("changeoldpass"))
                {
                    msg = "Old Password is required";
                }
//                else if (from.equalsIgnoreCase("cpass"))
//                {
//                    msg = "Confirm Password is required";
//                }
                else {
                    msg = "Password is required";
                }


                return msg;
            }
        }
        int length = password.length();
        if(length<6 || length>12)
        {
            msg = "Password must be at least 6 characters";
        }
        return msg;
    }

    // Confirm Password
    public static String confirmPasswordValidate(String cpassword, String password, boolean isBlankCheck,String message)
    {
        String msg = SUCCESS;

        if(isBlankCheck)
        {
            if (cpassword == null || cpassword.isEmpty()) {

                    msg = "Confirm Password is required";

                return msg;
            }
        }
        int length = cpassword.length();
        if(length<6 || length>12)
        {
            msg = "Password must be at least 6 characters";
            return msg;
        }

        if(!cpassword.equals(password))
        {
            msg = message;
            return msg;
        }
        return msg;
    }


    public static String confirmEmailValidate(String cemail, String email, boolean isBlankCheck)
    {
        String msg = SUCCESS;

        if(isBlankCheck) {
            if (cemail == null || cemail.isEmpty()) {
                msg ="Email is required";;
                return msg;
            }
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!cemail.matches(emailPattern)) {
            msg ="You have entered invalid email id";;
            return msg;
        }

        if(!cemail.equals(email))
        {
            msg = CEMAIL_MSG;
            return msg;
        }
        return msg;
    }
}
