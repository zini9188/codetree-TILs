import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int N;
    static int[][] map, tempMap;
    static boolean[][] visited;
    static int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
    static List<Group> groups;
    static int groupCount;
    static int totalValue;

    public static void main(String[] args) throws IOException {
        init();
        simulate();
    }

    private static void simulate() {
        for (int i = 0; i < 4; i++) {
            initValue();
            findGroups();
            findValue();
            rotate();
        }
        System.out.println(totalValue);
    }

    private static void initValue() {
        groups = new ArrayList<>();
        groupCount = 1;
        visited = new boolean[N][N];
    }

    private static void rotate() {
        rotateSquare(0, 0, N / 2 - 1, N / 2 - 1);
        rotateSquare(0, N / 2 + 1, N / 2 - 1, N - 1);
        rotateSquare(N / 2 + 1, 0, N - 1, N / 2 - 1);
        rotateSquare(N / 2 + 1, N / 2 + 1, N - 1, N - 1);
        rotateTen();
    }

    private static void rotateTen() {
        int[] temp = new int[N];
        System.arraycopy(map[N / 2], 0, temp, 0, N);
        for (int i = 0; i < N; i++) {
            map[N / 2][i] = map[i][N / 2];
        }

        for (int i = 0; i < N; i++) {
            map[i][N / 2] = temp[N - i - 1];
        }
    }

    private static void rotateSquare(int sx, int sy, int ex, int ey) {
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = sx; i <= ex; i++) {
            for (int j = sy; j <= ey; j++) {
                q.add(map[i][j]);
            }
        }

        for (int i = ey; i >= sy; i--) {
            for (int j = sx; j <= ex; j++) {
                map[j][i] = q.poll();
            }
        }
    }

    private static void findValue() {
        int size = groups.size();
        int[][] count = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (Point point : groups.get(i).points) {
                count[i][tempMap[point.x][point.y] - 1]++;
            }
        }

        int initialTotalValue = 0;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (count[i][j] == 0) {
                    continue;
                }

                initialTotalValue +=
                        (groups.get(i).cnt + groups.get(j).cnt) *
                                groups.get(i).num *
                                groups.get(j).num *
                                count[i][j];
            }
        }
        totalValue += initialTotalValue;
    }

    private static void findGroups() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) {
                    bfs(i, j);
                }
            }
        }
    }

    private static void init() throws IOException {
        N = Integer.parseInt(br.readLine());

        map = new int[N][N];
        tempMap = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }

    private static void bfs(int x, int y) {
        Queue<Point> q = new ArrayDeque<>();
        q.add(new Point(x, y));
        visited[x][y] = true;
        int cnt = 1;
        List<Point> points = new ArrayList<>();
        while (!q.isEmpty()) {
            Point cur = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];

                if (outRange(nx, ny)) {
                    continue;
                }

                if (map[x][y] != map[nx][ny]) {
                    points.add(new Point(nx, ny));
                    continue;
                }

                if (visited[nx][ny]) {
                    continue;
                }

                cnt++;
                visited[nx][ny] = true;
                tempMap[nx][ny] = groupCount;
                q.add(new Point(nx, ny));
            }
        }

        groups.add(new Group(groupCount, map[x][y], cnt, points));
        tempMap[x][y] = groupCount;
        groupCount++;
    }

    private static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    static class Point {

        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    static class Group {

        int idx, num, cnt;
        List<Point> points;

        public Group(int idx, int num, int cnt, List<Point> points) {
            this.idx = idx;
            this.num = num;
            this.cnt = cnt;
            this.points = points;
        }

        @Override
        public String toString() {
            return "Group{" +
                    "idx=" + idx +
                    ", num=" + num +
                    ", cnt=" + cnt +
                    ", points=" + points +
                    '}';
        }
    }
}