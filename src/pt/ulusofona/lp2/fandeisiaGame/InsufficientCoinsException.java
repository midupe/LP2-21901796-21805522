package pt.ulusofona.lp2.fandeisiaGame;

public class InsufficientCoinsException extends Throwable {
    public boolean teamRequiresMoreCoins(int teamId){
        //implementar
        return false;
    }
    public int getRequiredCoinsForTeam(int teamID){
        //Implementar
        return 30;
    }
}
