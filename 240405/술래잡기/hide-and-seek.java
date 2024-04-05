import java.io.*;
import java.util.*;

public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static int n, m, h, k;
    static StringTokenizer st;
    // 술래는 상 우 하 좌
    static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    static List<Integer>[][] map;
    static int[][] tree;
    static boolean flag = false;
    static int score, t;
    static boolean[] isGone;
    static Domangja[] domangjas;
    static String[] ddd = new String[]{"상", "우", "하", "좌"};

    public static void main(String[] args) throws IOException {
        // 술래잡기 게임
        // N*N 크기의 격자
        // 술래는 정중앙에 서있음 ->
        // m명의 도망자
        // 종류
        // 좌우로만 움직이는 유형 -> 항상 오른쪽 보고 시작
        // 상하로만 움직이는 유형 -> 항상 아래쪽 보고 시작

        // h개의 나무
        // 초기에 겹쳐져서 주어지는 것 가능

        // m명의 도망자가 먼저 동시에 움직임
        // 술래 움직임
        // k번 반복

        // 술래와의 거리가 3이하인 도망자만 움직임
        // 거리 측정 : | x1 - x2 | + | y1 - y2|

        // 도망자는 1턴간 다음 규칙에 따라 움직임
        // 바라보는 방향으로 1칸 움직일때 격자를 벗어나지 않는 경우
        // 1. 술래가 있으면 움직이지 않음
        // 2. 술래가 없으면 움직임, 나무 있어도 괜찮

        // 격자 벗어나는 경우
        // 먼저 방향을 반대로 틀음
        // 바라보는 방향으로 한 칸 움직여서 술래가 없으면 이동

        // -술래-
        // 술래는 처음 위 방향으로 시작하여 달팽이 모양으로 이동
        // 끝에 도달하면 다시 거꾸로 중심을 이동하고
        // 중심에 오면 처음처럼 위 방향으로 시작하여 시계방향으로 도는것을
        // K턴에 걸쳐 반복

        // 1번의 턴동안 한 칸 해당하는 방향으로 이동
        // 이동 후 만약 방향이 틀어지는 지점이면 바로 틀어줌
        // 양끝에 해당하는 1,1 혹은 정중앙에 도달하면 역시 틀어야함
        // 이동 직후 술래는 턴을 넘기기 전에 시야 내에 있는 도망자 잡음
        // 시야는 바라보는 방향을 기준으로 현재 칸 포함하여 항상 3칸
        // 하지만 나무가 있으면 해당 칸은 보이지 않음

        // 잡힌 도망자는 사라짐. 술래는 현재 턴을 t번째 턴이라고 할 때
        // t x 잡힌 도망자의 수 만큼의 점수를 얻음

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        map = new ArrayList[n + 1][n + 1];
        tree = new int[n + 1][n + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                map[i][j] = new ArrayList<>();
            }
        }
        isGone = new boolean[m + 1];
        domangjas = new Domangja[m + 1];

        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            map[x][y].add(i);
            domangjas[i] = new Domangja(i, x, y, d);
        }
        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        tree[x][y] = 1;

        Soolae soolae = new Soolae();

        Deque<Point> dq = new ArrayDeque<>();
        for (int i = 0; i < n * n; i++) {
            dq.add(soolae.move());
        }
        dq.addLast(new Point((n + 1) / 2, (n + 1) / 2, 0));

        soolae = new Soolae();
        for (int i = 1; i <= k; i++) {
            for (int j = 1; j <= m; j++) {
                if (isGone[j]) {
                    continue;
                }

                int dist = clac(domangjas[j], soolae);
                if (dist <= 3) {
                    domangjas[j].move(soolae);
                }
            }

            if (soolae.x == n / 2 && soolae.y == n / 2) {
                flag = false;
                dq.addLast(dq.pollFirst());
            } else if (soolae.x == 0 && soolae.y == 0) {
                flag = true;
                dq.addFirst(dq.pollLast());
            }

            if (!flag) {
                Point point = dq.pollFirst();
                soolae.move2(point);
                dq.addLast(point);
            } else {
                Point point = dq.pollLast();
                soolae.move2(point);
                dq.addFirst(point);
            }


            soolae.find(i);
        }

        System.out.println(score);
    }

    private static int clac(Domangja domangja, Soolae soolae) {
        return Math.abs(domangja.x - soolae.x) + Math.abs(domangja.y - soolae.y);
    }

    // 술래
    static class Soolae {
        // a = 회전해야하는 최대 횟수
        // b = 해당 방향으로 간 횟수
        // c = 회전 횟수
        int x, y, dir, a, b, c;

        public Soolae() {
            x = (n + 1) / 2;
            y = (n + 1) / 2;
            dir = 0;
            a = 1;
            b = 0;
            c = 0;
        }

        // 술래는 1,1,2,2,3,3,4,4,5,5의 형태로 증가함
        public Point move() {
            // 앞으로 감
            x += dx[dir];
            y += dy[dir];
            b++;
            // 옆에 봄
            if (b == a) {
                dir = (dir + 1) % 4;
                b = 0;
                c++;
            }

            // 두번 회전하면 a값 증가
            if (c == 2) {
                c = 0;
                a++;
            }

            return new Point(x, y, dir);
        }

        public void move2(Point point) {
            this.x = point.x;
            this.y = point.y;
            this.dir = flag ? (point.d + 2) % 4 : point.d;
        }

        public void find(int t) {
            int cnt = 0;
            for (int i = 0; i < 3; i++) {
                int nx = x + dx[dir] * i;
                int ny = y + dy[dir] * i;
                if (outRange(nx, ny)) {
                    continue;
                }

                for (int j = map[nx][ny].size() - 1; j >= 0; j--) {
                    if (tree[nx][ny] == 0) {
                        Integer next = map[nx][ny].get(j);
                        isGone[next] = true;
                        cnt++;
                        map[nx][ny].remove(next);
                    }
                }
            }
            score += t * cnt;
        }

        @Override
        public String toString() {
            return "Soolae{" +
                    "x=" + x +
                    ", y=" + y +
                    ", dir=" + ddd[dir] +
                    ", a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    ", flag=" + flag +
                    '}';
        }
    }

    static class Domangja {
        int idx, x, y, d, type;

        public Domangja(int idx, int x, int y, int type) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.d = type == 1 ? 1 : 2;
            this.type = type;
        }


        public void move(Soolae soolae) {
            int nx = x + dx[d];
            int ny = y + dy[d];

            // 격자 벗어나는 경우
            if (outRange(nx, ny)) {
                d = (d + 2) % 4;
                nx = x + dx[d];
                ny = y + dy[d];

            }

            if (!(soolae.x == nx && soolae.y == ny)) {
                map[x][y].remove((Integer) idx);
                x = nx;
                y = ny;
                map[x][y].add(idx);
            }
        }

        @Override
        public String toString() {
            return "Domangja{" +
                    "idx=" + idx +
                    ", x=" + x +
                    ", y=" + y +
                    ", d=" + ddd[d] +
                    ", type=" + type +
                    '}';
        }
    }

    static class Point {
        int x, y, d;

        public Point(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", d=" + ddd[d] +
                    '}';
        }
    }


    private static boolean outRange(int x, int y) {
        return x < 1 || y < 1 || x > n || y > n;
    }
}