import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    // [5 ≤ n ≤ 99] [1 ≤ m, h ≤ n^2] [1 ≤ k ≤ 100] [1 ≤ x, y ≤ n]
    // n: 맵의 크기     m: 도망자의 수   h: 나무의 수    k: 턴의 수
    static int n, m, h, k;
    static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    static It it;
    static Set<Integer>[][] map;
    static Runner[] runners;
    static boolean[][] tree;

    public static void main(String[] args) throws IOException {
        init();
        simulate();
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        map = new Set[n + 1][n + 1];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                map[i][j] = new HashSet<>();
            }
        }

        // 술래
        it = new It();

        // 도망자
        runners = new Runner[m + 1];
        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            runners[i] = new Runner(i, x, y, d);
            map[x][y].add(i);
        }

        // 나무
        tree = new boolean[n + 1][n + 1];
        for (int i = 0; i < h; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            tree[x][y] = true;
        }
    }

    private static void simulate() {
        for (int i = 1; i <= k; i++) {
            for (int j = 1; j <= m; j++) {
                Runner runner = runners[j];
                if (runner.die) {
                    continue;
                }
                runner.move();
            }

            it.move();
            it.search(i);
        }

        System.out.println(it.score);
    }

    static int calcDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    static boolean outRange(int x, int y) {
        return x < 1 || y < 1 || x > n || y > n;
    }

    static class Runner {

        int idx, x, y, direction, d;
        boolean die;

        //d가 1인 경우 좌우로 움직임을
        //2인 경우 상하로만 움직임을
        //좌우로 움직이는 사람은 항상 오른쪽 1
        //상하로 움직이는 사람은 항상 아래쪽 2
        public Runner(int idx, int x, int y, int d) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.direction = d == 1 ? 1 : 2;
            this.d = d;
            die = false;
        }

        // 술래와의 거리가 3 이하인 도망자만
        // 두 사람간의 거리는 |x1 - x2| + |y1 - y2|
        public boolean isNearByIt() {
            int distance = calcDistance(x, y, it.x, it.y);
            return distance <= 3;
        }

        //술래와의 거리가 3 이하인 도망자들은 1턴 동안 다음 규칙에 따라 움직이게 됩니다.
        public void move() {
            if (!isNearByIt()) {
                return;
            }

            //현재 바라보고 있는 방향으로 1칸 움직인다 했을 때 격자를 벗어나지 않는 경우
            //움직이려는 칸에 술래가 있는 경우라면 움직이지 않습니다.
            int nx = x + dx[direction];
            int ny = y + dy[direction];

            //현재 바라보고 있는 방향으로 1칸 움직인다 했을 때 격자를 벗어나는 경우 방향을 반대로 틀어줍니다
            if (outRange(nx, ny)) {
                direction = (direction + 2) % 4;
                nx = x + dx[direction];
                ny = y + dy[direction];
            }

            //움직이려는 칸에 술래가 있지 않다면 해당 칸으로 이동합니다. 해당 칸에 나무가 있어도 괜찮습니다.
            if (!(nx == it.x && ny == it.y)) {
                map[x][y].remove(idx);
                x = nx;
                y = ny;
                map[x][y].add(idx);
            }
        }
    }

    static class It {

        int x, y, look, cnt, moveIndex, score;
        int[] move;
        boolean flag;

        public It() {
            x = (n + 1) / 2;
            y = (n + 1) / 2;
            look = 0;
            cnt = 0;
            moveIndex = 0;
            move = new int[n * 2 - 1];
            flag = false;
            score = 0;

            int dist = 1;
            for (int i = 0; i < n * 2 - 2; i += 2) {
                move[i] = move[i + 1] = dist++;
            }
            move[n * 2 - 2] = dist - 1;
        }

        public void move() {
            x = x + dx[look];
            y = y + dy[look];
            cnt++;

            // 움직이는 방향으로 개수 다 채우면
            if (cnt == move[moveIndex]) {
                if (flag) {
                    moveIndex--;
                    look = (look + 4 - 1) % 4;
                } else {
                    moveIndex++;
                    look = (look + 1) % 4;
                }
                cnt = 0;
            }
            // 상 우 하 좌
            // 하 우 상 좌

            // 중앙에선 위를
            if (x == (n + 1) / 2 && y == (n + 1) / 2) {
                look = 0;
                moveIndex = 0;
                flag = false;
            }
            // 1,1에선 아래를 봐야함
            else if (x == 1 && y == 1) {
                look = 2;
                moveIndex = n * 2 - 2;
                flag = true;
            }
        }

        public void search(int turn) {
            for (int i = 0; i < 3; i++) {
                int nx = x + dx[look] * i;
                int ny = y + dy[look] * i;

                // 밖이거나 나무 있으면 안봄
                if (outRange(nx, ny) || tree[nx][ny]) {
                    continue;
                }

                // 도망자 있으면 점수 더함
                if (!map[nx][ny].isEmpty()) {
                    score += turn * map[nx][ny].size();
                    for (Integer index : map[nx][ny]) {
                        runners[index].die = true;
                    }
                    map[nx][ny] = new HashSet<>();
                }
            }
        }
    }
}