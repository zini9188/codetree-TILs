import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
    static StringTokenizer st;
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(br.readLine());

        List<Long> stores = new ArrayList<>();
        st = new StringTokenizer(br.readLine());
        while (st.hasMoreTokens()) {
            stores.add(Long.parseLong(st.nextToken()));
        }

        st = new StringTokenizer(br.readLine());
        long a = Long.parseLong(st.nextToken());
        long b = Long.parseLong(st.nextToken());

        long e = 0;
        for (long store : stores) {
            double c = (double) (store - a) / b;
            long d = (long) c;

            if(c - d > 0){
                d++;
            }

            if(d > e){
                e = d;
            }
        }

        e++;
        sb.append(e * stores.size());
        bw.write(sb.toString());
        bw.close();
        br.close();
    }
}