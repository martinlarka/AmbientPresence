package nu.larka.ambientpresence;

/**
 * Created by martin on 15-03-12.
 */
public class Housing {
    private String name;
    private String UID;
    private Boolean away;
    private String eta;
    private Long ambientT;
    private Long targetT;

    public Housing() {}

    public Housing(String name, Boolean away, String eta, Long ambientT, Long targetT) {
        this.name = name;
        this.away = away;
        this.eta = eta;
        this.ambientT = ambientT;
        this.targetT = targetT;
    }

    public String getName() {
        return name;
    }

    public Boolean getAway() {
        return away;
    }

    public String getEta() {
        return eta;
    }

    public Long getAmbientT() {
        return ambientT;
    }

    public Long getTargetT() {
        return targetT;
    }
}
