import java.sql.Date;

/**
 * Creates a Client object with all needed values so
 * only one SELECT call to the database is needed, and
 * safer methods of getting client data are implemented
 * (Eventually, Pipeline.Java could also connect here.)
 * @author Julia Scott
 */
public class Client {
    private int id;
    private String name;
    private Date Bdate;
    private Date dateA;
    private String note;
    private String company;
    private String source;
    private String Mailing_Addr;
    private Boolean Rec_PassPrt;


    public Client(int id, String name, String Mailing_Addr, Boolean Rec_PassPrt, Date Bdate, Date DateA, String company, String Note, String source) {
        this.id = id;
        this.name = name;
        this.Mailing_Addr = Mailing_Addr;
        this.Rec_PassPrt = Rec_PassPrt;
        this.Bdate = Bdate;
        this.dateA = DateA;
        this.note = Note;
        this.company = company;
        this.source = source;
    }

//---------------methods to replace direct SELECT calls-----------------//
    public int getId() {return id;}
    public String getName() {return name;}
    public String getMail_Addr() {return Mailing_Addr;}
    public boolean getPassPrt() {return Rec_PassPrt;}
    public Date getBdate() {return Bdate;}
    public Date getDateA() {return dateA;}
    public String getNote() {return note;}
    public String getCompany() {return company;}
    public String getSource() {return source;}

    /**
     * Formats what list says in the UI
     * @return Name of client and the date that client was added
     * ex: Julia (added: 2025-01-01)
     */
    @Override
    public String toString() {
        //TODO: Date Added is always going to be not null. A dateSafe check is probably redundant
        String dateSafe = (dateA != null) ? dateA.toString() : "No Date";
        return name + " (added: " + dateSafe + ")";
    }
}