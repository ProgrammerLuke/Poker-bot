package Players.Bots;

public abstract class BasicBot {
    //input methods
    public abstract void givePlayers(int numPlayers); //not necessary
    public abstract void giveHand(String[] hand);//gives the player his hand
    public abstract void givePlayerCards(String[][] otherCards, int thisPlayer);//gives the player another players cards
    public abstract void giveFlop(String[] flop);//gives the player the flop
    public abstract void giveTurn(String turn);//gives the player the turn
    public abstract void giveRiver(String river);//gives the player the river
    public abstract void otherBets(int[] bets);//gives the player the other players bets
    public abstract void giveMoney(int money);//gives the player and initial buy in
    //output methods
    public abstract int blind(int blindAmount);//forced blind bet
    public abstract int bet(int call);//function used to askt the bot to bet. fold is denoted as -1, check is denoted as 0
    public abstract int getBalance();//returns the players total balance. This is kept track of inside the bot, but an honor system will be necessary for keeping track of that
    public abstract String[] getCards();//returns the players cards. This will also be kept track of outside the bot, so no cheating or you are disqualified
}
