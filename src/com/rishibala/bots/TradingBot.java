package com.rishibala.bots;

import com.rishibala.server.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TradingBot {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 3000); //change localhost if on different ip
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner s = new Scanner(System.in);

            int botId = 0;
            User user = new User();

            try {
                String serverMessage = in.readLine();
                if(serverMessage != null) {
//                    System.out.println(serverMessage);
                    String[] arg = serverMessage.split(",");
                    botId = Integer.parseInt(arg[0]);
                    user = User.unString(arg[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(botId == -1 || user.getBotId() <= 0) {
                System.out.println(botId);
                System.out.println(user);
                System.out.println("error");
                return;
            }

            while(true) {
                System.out.println();
                System.out.println("-".repeat(30));

                System.out.println("""
                       Current Orders (o)
                       New Order (n)
                       Cancel order (c)
                       Portfolio (p)
                       Quit (q)
                        """);
                System.out.println("-".repeat(20));

                do {
                    try {
                        String serverMessage;
                        if (in.ready()) {
                            if ((serverMessage = in.readLine()) != null) {
                                if (serverMessage.contains("Found match")) {
                                    System.out.println();
                                    String[] args1 = serverMessage.split("-");
                                    for (String arg : args1) {
                                        System.out.println(arg);
                                    }
                                    System.out.println();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                } while (!(System.in.available() > 0));

                String choice = s.nextLine().substring(0, 1).toLowerCase();

                if(choice.equals("o")) {
                    out.println("listedmatches");
                    try {
                        String serverMessage = in.readLine();
                        if(serverMessage != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.println("close");
                    out.flush();
                } else if (choice.equals("n")) {
                    String buySell = "";
                    do {
                        System.out.print("Buy or sell: ");
                        buySell = s.nextLine().substring(0, 1).toLowerCase();

                    } while (!buySell.equalsIgnoreCase("b") && !buySell.equalsIgnoreCase("s"));

                    if(buySell.equalsIgnoreCase("b")) buySell = "BUY";
                    if(buySell.equalsIgnoreCase("s")) buySell = "SELL";

                    double price = 0;
                    while(!(price > 0)) {
                        System.out.print("Price: ");
                        try {
                            double temp = Double.parseDouble(s.nextLine());
                            price = temp;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    int qty = 0;
                    while(!(qty > 0)) {
                        System.out.print("Quantity: ");
                        try {
                            int temp = Integer.parseInt(s.nextLine());
                            qty = temp;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    out.println(botId + ", " + buySell + ", " + price + ", " + qty);
                    out.flush();
                } else if(choice.equals("c")) {
                    int order = 0;
                    while(!(order > 0)) {
                        System.out.print("Order Number: ");
                        try {
                            order = s.nextInt();
                            out.println("haveOrder-" + order);
                            boolean check = false;
                            try {
                                String serverMessage = in.readLine();
                                if(serverMessage.equalsIgnoreCase("true")) {
                                    check = true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(!check) {
                                order = 0;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    out.println("cancel," + order);
                    System.out.println("Cancelled Order " + order);
                    s.nextLine();
                    out.println("close");
                    out.flush();
                } else if(choice.equals("p")) {
                    out.println("userRequest");
                    try {
                        String serverMessage = in.readLine();
                        if(serverMessage != null) {
                            user = User.unString(serverMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println();
                    System.out.println(user.getStockAmt() + " shares.");
                    out.println("close");
                    out.flush();
                } else if(choice.equals("q")) {
                    break;
                }

                try {
                    String serverMessage;
                    if((serverMessage = in.readLine()) != null) {
                        if(serverMessage.contains("Found match")) {
                            System.out.println();
                            String[] args1 = serverMessage.split("-");
                            for(String arg : args1) {
                                System.out.println(arg);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}