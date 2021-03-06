package GBN;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Client {
    private static String serverHostName;
    private static int serverPort;
    private static String fileName;
    private static int windowSize;
    private static int mss;

    public static List<String> segmentationFunction(String rfcString, int mss) {
        ByteArrayInputStream in = new ByteArrayInputStream (rfcString.getBytes());
        byte[] buffer = new byte[mss];
        List<String> segmentArray = new ArrayList<String>();
        int len;
        try {
            while ((len = in.read(buffer)) > 0) {
                byte[] buffer1;
                if(len < mss) {
                    buffer1 = new byte[len];
                    for(int i = 0; i < len; i++) {
                        buffer1[i] = buffer[i];
                    }
                } else {
                    buffer1 = buffer;
                }
                String s = "";
                for (byte b : buffer1) {
                    s+= ((char) b);
                }
                segmentArray.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return segmentArray;
    }

    public static void main(String[] args) throws IOException {
        serverHostName = args[0];
        serverPort = Integer.parseInt(args[1]);
        fileName = args[2];
        windowSize = Integer.parseInt(args[3]);
        mss = Integer.parseInt(args[4]);

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String rfcString = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            rfcString = sb.toString();
        } finally {
            br.close();
        }

        int var1 = mss * 10;
        int seqNum = 0;
        List<String> segStringArray = segmentationFunction(rfcString, var1);
        List<Segment> segmentArray = new ArrayList<Segment>();
        Iterator<String> it = segStringArray.iterator();

        while(it.hasNext()) {
            String segment = (String) it.next();
            segmentArray.add(new Segment(segment, var1, seqNum++));
        }


        long startTime = System.currentTimeMillis();
        System.out.println("startTime:" + startTime);
        GoBackN gbn = new GoBackN(serverHostName, serverPort, segmentArray, windowSize);
        gbn.rdt_send();


//        System.out.println(endTime - startTime);


    }

}
