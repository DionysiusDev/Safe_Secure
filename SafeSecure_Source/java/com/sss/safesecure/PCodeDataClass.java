package com.sss.safesecure;

/**
 * Purpose: this class handles the temporary storage and getting / setting of the user pass code.
 */
public class PCodeDataClass {

    /**
     * Purpose: String for storing passcode data.
     *
     * */
    private String passcode;

    /**
     * Purpose: A constructor for the class: PCodeDataClass.
     *
     * @param pc (String passcode)
     * ----------------------------------------------------------
     */
    public PCodeDataClass(final String pc) {

        passcode = pc;
    }

    /**
     * Purpose: A method that will allow the calling class to set the
     * properties for the pass code and key.
     *
     * @param pc (String passcode)
     * ----------------------------------------------------------
     */
    public void setPCInfo(final String pc) {
        passcode = pc;
    }

    /**
     * Purpose: A method that will allow the calling class to set the pass code
     * property.
     *
     */
    public final void setPC() {
        String pc = passcode;
    }

    /**
     * Purpose: A method that will allow this PCodeDataClass class to provide the calling class
     * with the passcode data.
     *
     * @return passcode.
     * ----------------------------------------------------------
     */
    public final String getPC() {
        return passcode;
    }
}