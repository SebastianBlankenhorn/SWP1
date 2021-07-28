package de.hft.ip1.GameOfLife;

import de.hft.ip1.GameOfLife.Client.GameOfLifeForm;
import de.hft.ip1.GameOfLife.Server.IServer;
import de.hft.ip1.GameOfLife.Server.Server;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LauncherMain {

    public static void main(String[] args) throws RemoteException {
        ArrayList<String> argList = new ArrayList<String>((Collection<? extends String>) Arrays.stream(args).map(String::toLowerCase).collect(Collectors.toList()));
        if (argList.contains("--server") || argList.contains("-s")) {
            runAsServer(argList);
        } else if (argList.contains("--client") || argList.contains("-c")) {
            runAsClient(argList);
        } else {
            runAsClient(argList);
        }
    }


    /**
     * Use existing or create a new registry and provide the server
     *
     * @param argList
     */
    public static void runAsServer(ArrayList<String> argList) {
        int portIndex = Math.max(argList.indexOf("--port"), argList.indexOf("-p")) + 1;
        if (portIndex == 0 || argList.size() <= portIndex) {
            throw new IllegalArgumentException("Missing argument for port");
        }
        int port;
        try {
            port = Integer.parseInt(argList.get(portIndex));

            Registry registry = LocateRegistry.getRegistry(port);
            try {
                registry.lookup(null);
            } catch (RemoteException ex) {
                registry = LocateRegistry.createRegistry(port);
            } catch (Exception ignored) {
            }

            IServer server = new Server();
            UnicastRemoteObject.exportObject(server, 0);

            registry.rebind(IServer.class.getName(), server);
            System.out.println("Server gestartet!");

            Scanner scanner = new Scanner(System.in);

            String input = scanner.nextLine();

            if (input.equals("stop")) {
                System.out.println("versuche Server zu stoppen...");
                registry.unbind(IServer.class.getName());
                UnicastRemoteObject.unexportObject(server, true);
                System.out.println("Server gestoppt");
                System.exit(0);
            }

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal-formed argument for port");
        } catch (RemoteException e) {
            System.err.println("Ein Fehler ist aufgetreten");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the Client
     *
     * @param argList
     * @throws RemoteException
     */
    private static void runAsClient(ArrayList<String> argList) throws RemoteException {
        EventQueue.invokeLater(GameOfLifeForm::new);
    }
}
