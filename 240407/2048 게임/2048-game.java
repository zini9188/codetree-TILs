import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int N;
    static int[][] map;
    static int ans = 0;

    public static void main(String[] args) throws IOException {
        init();
        simulate();
    }

    private static void simulate() {
        Queue<int[][]> q = new ArrayDeque<>();
        q.add(map);

        for (int i = 0; i < 5; i++) {
            int size = q.size();
            for (int j = 0; j < size; j++) {
                int[][] cur = q.poll();
                for (int k = 0; k < 4; k++) {
                    q.add(push(cur, k));
                }
            }
        }
        System.out.println(ans);
    }

    private static int[][] push(int[][] m, int dir) {
        if (dir == 0) {
            return pushUp(m);
        } else if (dir == 1) {
            return pushDown(m);
        } else if (dir == 2) {
            return pushLeft(m);
        }
        return pushRight(m);
    }

    private static int[][] pushDown(int[][] m) {
        int[][] temp = new int[N][N];
        Queue<Integer> q = new ArrayDeque<>();
        for (int y = 0; y < N; y++) {
            for (int x = N - 1; x >= 0; x--) {
                if (m[x][y] > 0) {
                    q.add(m[x][y]);
                }
            }

            int x = N - 1;
            while (!q.isEmpty()) {
                int first = q.poll();
                if (!q.isEmpty()) {
                    // 둘이 같으면
                    if (q.peek() == first) {
                        temp[x][y] = first + q.poll();
                    } else {
                        temp[x][y] = first;
                    }
                } else {
                    temp[x][y] = first;
                }
                if(temp[x][y] > ans){
                    ans = temp[x][y];
                }
                x--;
            }
        }
        return temp;
    }

    private static int[][] pushLeft(int[][] m) {
        int[][] temp = new int[N][N];
        Queue<Integer> q = new ArrayDeque<>();
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                if (m[x][y] > 0) {
                    q.add(m[x][y]);
                }
            }

            int y = 0;
            while (!q.isEmpty()) {
                int first = q.poll();
                if (!q.isEmpty()) {
                    // 둘이 같으면
                    if (q.peek() == first) {
                        temp[x][y] = first + q.poll();
                    } else {
                        temp[x][y] = first;
                    }
                } else {
                    temp[x][y] = first;
                }
                if(temp[x][y] > ans){
                    ans = temp[x][y];
                }
                y++;
            }
        }
        return temp;
    }

    private static int[][] pushRight(int[][] m) {
        int[][] temp = new int[N][N];
        Queue<Integer> q = new ArrayDeque<>();
        for (int x = 0; x < N; x++) {
            for (int y = N - 1; y >= 0; y--) {
                if (m[x][y] > 0) {
                    q.add(m[x][y]);
                }
            }

            int y = N - 1;
            while (!q.isEmpty()) {
                int first = q.poll();
                if (!q.isEmpty()) {
                    // 둘이 같으면
                    if (q.peek() == first) {
                        temp[x][y] = first + q.poll();
                    } else {
                        temp[x][y] = first;
                    }
                } else {
                    temp[x][y] = first;
                }
                if(temp[x][y] > ans){
                    ans = temp[x][y];
                }
                y--;
            }
        }
        return temp;
    }

    private static int[][] pushUp(int[][] m) {
        int[][] temp = new int[N][N];
        Queue<Integer> q = new ArrayDeque<>();
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                if (m[x][y] > 0) {
                    q.add(m[x][y]);
                }
            }

            int x = 0;
            while (!q.isEmpty()) {
                int first = q.poll();
                if (!q.isEmpty()) {
                    // 둘이 같으면
                    if (q.peek() == first) {
                        temp[x][y] = first + q.poll();
                    } else {
                        temp[x][y] = first;
                    }
                } else {
                    temp[x][y] = first;
                }
                if(temp[x][y] > ans){
                    ans = temp[x][y];
                }
                x++;
            }
        }

        return temp;
    }

    private static void init() throws IOException {
        N = Integer.parseInt(br.readLine());
        map = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }
}