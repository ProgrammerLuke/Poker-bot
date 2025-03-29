package Players.Bots;

import java.util.Scanner;

public class ExternalPlayer extends BasicBot{
    //variables for keeping track of player stuff
    private String[] cards = new String[2];
    private int balance = 0;
    private String[] table = new String[5];
    private int[] bets = new int[] {-2, -2, -2, -2, -2, -2};
    private Scanner input = new Scanner(System.in);


    //betting method
    @Override
    public int bet(int call) {
        //get input for the bet
        System.out.println("Enter your bet (C to see all cards, B to see other players bets, and M to see your balance)\n" + call + " to call:  ");
        String bet = input.nextLine();
        int betAmount = 0;
        //convert the input to an integer, or give the requested data, then sets betAmount to bet(int call) through recursion.
        // This will allow a near infinite amount of rereading information
        switch(bet){
            case "T":
                System.out.println(cards[0] + cards[1] + table[0] + table[1] + table[2] + table[3] + table[4]);
                betAmount = bet(call);
                break;
            case "B":   
                System.out.println(bets[0] + " " + bets[1] + " " + bets[2] + " " + bets[3] + " " + bets[4] + " " + bets[5]);
                betAmount = bet(call);
                break;
            case "M":
                System.out.println(balance);
                betAmount = bet(call);
                break;
            default:
                try{
                    betAmount = Integer.parseInt(bet);
                    break;
                }catch(Exception e){
                    System.out.println("Invalid input.");
                    betAmount = bet(call);
                    break;
                }
            
        }
        //check the validity of the bet
        if(betAmount >= call || betAmount == balance){
            return betAmount;
        }else if(betAmount < call){
            System.out.println("You must bet at least " + call + " or your balance.");
            betAmount = bet(call);
        }else if(betAmount > balance){
            System.out.println("Insufficient Funds. Please enter a smaller amount.");
            betAmount = bet(call);
        }
        return betAmount;
    }

    @Override
    public String[] getCards() {
        // returns the players cards
        return cards;
        
    }

    @Override
    public void giveHand(String[] hand) {
        // create the players hand
        cards = hand;
        
    }

    @Override
    public void givePlayers(int numPlayers) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void givePlayerCards(String[] cards, int player) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void giveFlop(String[] flop) {
        // TODO Auto-generated method stub
        table[0] = flop[0];
        table[1] = flop[1];
        table[2] = flop[2];
        
    }

    @Override
    public void giveTurn(String turn) {
        // TODO Auto-generated method stub
        table[3] = turn;
        
    }

    @Override
    public void giveRiver(String river) {
        // TODO Auto-generated method stub
        table[4] = river;
        
    }

    @Override
    public void otherBets(int[] bets) {
        // TODO Auto-generated method stub
        this.bets = bets;
        
    }

    @Override
    public void giveMoney(int money) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int blind(int blindAmount) {
        // TODO Auto-generated method stub
        return 0;
        
    }

    @Override
    public int getBalance() {
        // TODO Auto-generated method stub
        return 0;
    }
}