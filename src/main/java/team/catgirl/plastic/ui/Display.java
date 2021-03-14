package team.catgirl.plastic.ui;

public interface Display {
    /**
     * Display a status message
     * @param message to display
     */
    void displayStatus(String message);

    /**
     * Send a message to the chat console
     * @param message to send
     */
    void sendMessage(String message);
}
