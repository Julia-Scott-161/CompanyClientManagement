import java.sql.Date;

/**
 * Creates a Touchpoint object with all needed values so
 * only one SELECT call to the database is needed, and
 * safer methods of getting client data are implemented
 * (Eventually used in Add Touchpint and edit/view
 * Touchpoint UIs)
 * @author Julia Scott
 */
public class Touchpoint {
    private int P_id, score;
    private String TP_Type, note;
    private Date date;


    public Touchpoint(int P_id, Date TPdateocc, String TP_Type, int EngageScr, String TP_Note) {
        this.P_id = P_id;
        this.date = TPdateocc;
        this.TP_Type = TP_Type;
        this.score = EngageScr;
        this.note = TP_Note;
    }

//---------------methods to replace direct SELECT calls-----------------//
    public int getPrimary_Id() {return P_id;}
    public int getScore() {return score;}
    public String getType() {return TP_Type;}
    public String getNote() {return note;}
    public Date getDate() {return date;}

    /**
     * Formats what list says in the UI
     * @return type of touchpoint and the date it was done
     * ex: Email (2025-01-01)
     */
    @Override
    public String toString() {
        return TP_Type + " (" + date.toString() + ")";
    }
}