import java.io.*;
import java.util.StringTokenizer;

public class Main {

    static final int TOP = 0, BOTTOM = 1, WEST = 2, EAST = 3, NORTH = 4, SOUTH = 5;
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int n, m, k;
    static int[][] map;
    // 동 서 북 남
    static int[] dx = {0, 0, -1, 1}, dy = {1, -1, 0, 0};

    public static void main(String[] args) throws IOException {
        init();
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        map = new int[n][m];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < m; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        Dice dice = new Dice(x, y);
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < k; i++) {
            int direction = Integer.parseInt(st.nextToken()) - 1;
            simulate(dice, direction);
        }
    }

    private static void simulate(Dice dice, int direction) {
        int num = dice.move(direction);

        if (num == -1) {
            return;
        }

        System.out.println(num);
    }

    static class Dice {

        // 위치
        int x, y;
        // 6면
        int[] number;

        public Dice(int x, int y) {
            this.x = x;
            this.y = y;
            this.number = new int[6];
        }

        public int move(int dir) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            if (outRange(nx, ny)) {
                return -1;
            }
            x = nx;
            y = ny;

            int up = number[TOP];
            if (dir == 0) {
                number[TOP] = number[WEST];
                number[WEST] = number[BOTTOM];
                number[BOTTOM] = number[EAST];
                number[EAST] = up;
            } else if (dir == 1) {
                number[TOP] = number[EAST];
                number[EAST] = number[BOTTOM];
                number[BOTTOM] = number[WEST];
                number[WEST] = up;
            } else if (dir == 2) {
                number[TOP] = number[SOUTH];
                number[SOUTH] = number[BOTTOM];
                number[BOTTOM] = number[NORTH];
                number[NORTH] = up;
            } else if (dir == 3) {
                number[TOP] = number[NORTH];
                number[NORTH] = number[BOTTOM];
                number[BOTTOM] = number[SOUTH];
                number[SOUTH] = up;
            }

            // 칸에 쓰여진 수가 0이면
            if (map[x][y] == 0) {
                // 주사위 바닥면 쓰여진 수가 칸에 복사
                map[x][y] = number[BOTTOM];
            }
            // 칸에 쓰여진 수가 0이 아니면
            else {
                // 칸에 쓰여진 수가 정육면체 바닥면으로 복사
                // 칸은 0이됨
                number[BOTTOM] = map[x][y];
                map[x][y] = 0;
            }
            return number[TOP];
        }

        public boolean outRange(int x, int y) {
            return x < 0 || y < 0 || x >= n || y >= m;
        }
    }
}