/**
 * A pipeline, which will be either selected from a database call with the
 * needed client ID, or created upon adding a client.
 * As of now though, it is not implemented nor used
 * @author Julia Scott
 */
public class Pipeline {
    private int PLStg_ID, DaysInPipe;
    private String PipeLoc, LostCommt;

    public Pipeline(int P_ID, String PipeLoc, String LostCommt, int DaysInPipe) {
        this.PLStg_ID = P_ID;
        this.PipeLoc = PipeLoc;
        this.LostCommt = LostCommt;
        this.DaysInPipe = DaysInPipe;
    }

//---------------methods to replace direct SELECT calls-----------------//
    public int getPLStg_ID() {return PLStg_ID;}
    public String getPipeLoc() {return PipeLoc;}
    public String getLostCommt() {return LostCommt;}
    public int getPipeDays() {return DaysInPipe;}

    /**
     * Formats what pipeline says in the UI
     * @return Pipeline Location (since that's what I needed in ViewEditClientUI
     * ex: Lost
     */
    @Override
    public String toString() {
        return PipeLoc;
    }
}
