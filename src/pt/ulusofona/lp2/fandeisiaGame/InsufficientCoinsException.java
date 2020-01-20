package pt.ulusofona.lp2.fandeisiaGame;

public class InsufficientCoinsException extends Exception {
    int requiredLDR;
    int requiredRESISTENCIA;

    public InsufficientCoinsException(int requiredLDR, int requiredRESISTENDICA){
        this.requiredLDR = requiredLDR;
        this.requiredRESISTENCIA = requiredRESISTENDICA;
    }

    public boolean teamRequiresMoreCoins(int teamId){
        if (getRequiredCoinsForTeam(teamId) > 50) {
            return true;
        }
        return false;
    }
    public int getRequiredCoinsForTeam(int teamID){
        if (teamID == 10) {
            return requiredLDR;
        } else {
            return requiredRESISTENCIA;
        }
    }
}
