/**
 * For the AddContact class and to generate the list in ViewEditClientUI
 * Was intended for view/edit contact UI as well
 * @author Julia Scott
 */
public class Contact {
    private int Contact_ID;
    private String Contact_Info, Cnt_Type;
    private boolean Pref_Cont;



    public Contact(int Contact_id, String Cnt_Type, boolean Pref_Cont, String Contact_Info) {
        this.Contact_ID = Contact_id;
        this.Cnt_Type = Cnt_Type;
        this.Pref_Cont = Pref_Cont;
        this.Contact_Info = Contact_Info;
    }

//---------------methods to replace direct SELECT calls-----------------//
    public int getContactId() {return Contact_ID;}
    public String getType() {return Cnt_Type;}
    public boolean getPref() {return Pref_Cont;}
    public String getInfo() {return Contact_Info;}

    /**
     * Formats what list says in the UI
     * @return Type of contact and the info for that contact
     * ex: Phone: 999-999-9999
     */
    @Override
    public String toString() {
        return Cnt_Type + ": " + Contact_Info;
    }
}
