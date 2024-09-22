import java.io.*;
import java.util.*;

public class Main {

    private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private static int R;
    private static int C;
    private static int[][] board;
    private static Golem[] golems;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());

        board = new int[R + 3][C];

        golems = new Golem[k + 1];
        int score = 0;
        for (int i = 1; i <= k; i++) {
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());

            golems[i] = new Golem(i, d, new Point(1, c));
            move(golems[i]);
            if (golems[i].c.x <= 3) {
                board = new int[R + 3][C];
                continue;
            }
            golems[i].set();
            int exit = golems[i].exit();
            score += exit;
        }

        System.out.println(score);
    }

    private static void move(Golem golem) {
        boolean moved;
        do {
            moved = false;
            for (int i = 1; i <= 3; i++) {
                if (golem.canMove(i)) {
                    golem.move(i);
                    moved = true;
                    break;
                }
            }
        } while (moved);
    }

    private static boolean inRange(int x, int y) {
        return x >= 0 && x < R + 3 && y >= 0 && y < C;
    }

    private static class Golem {

        // 골렘은 5칸을 가짐.
        int idx, d;
        Point c;

        public Golem(int idx, int d, Point c) {
            this.idx = idx;
            this.d = d;
            this.c = c;
        }

        // 1. 남쪽으로 한칸
        // 2. 1이 불가능하면 서쪽방향으로 회전
        // 3. 1,2 둘다 안되면 동쪽으로 회전하며 내려감
        public void move(int step) {
            if (step == 1) {
                c = c.next(DOWN);
            } else if (step == 2) {
                c = c.next(LEFT);
                c = c.next(DOWN);
                d = (3 + d) % 4;
            } else if (step == 3) {
                c = c.next(RIGHT);
                c = c.next(DOWN);
                d = (d + 1) % 4;
            }
        }

        public boolean canMove(int step) {
            Point n;

            if (step == 1) {
                n = c.next(DOWN);
            } else if (step == 2) {
                n = c.next(LEFT);
                if (!check(n)) {
                    return false;
                }
                n = n.next(DOWN);
            } else {
                n = c.next(RIGHT);
                if (!check(n)) {
                    return false;
                }
                n = n.next(DOWN);
            }
            return check(n);
        }

        private boolean check(Point n) {
            for (int i = 0; i < 4; i++) {
                int nx = n.x + dx[i];
                int ny = n.y + dy[i];

                if (inRange(nx, ny)) {
                    if (board[nx][ny] != 0) {
                        if (board[nx][ny] == idx) {
                            continue;
                        }
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }

        // 이동 마친 후 칸에 기록해줌.
        public void set() {
            board[c.x][c.y] = idx;
            for (int i = 0; i < 4; i++) {
                board[c.x + dx[i]][c.y + dy[i]] = idx;
            }
        }

        // 현재 위치하는 출구가 다른 골렘과 인접하면 출구를 통해 다른 골렘으로 이동
        public int exit() {
            boolean[][] visited = new boolean[R + 3][C];

            int maxRow = 0;
            Queue<Point> q = new ArrayDeque<>();
            q.add(new Point(c.x, c.y));
            visited[c.x][c.y] = true;

            while (!q.isEmpty()) {
                Point cur = q.poll();
                int curGolemIdx = board[cur.x][cur.y];
                maxRow = Math.max(cur.x - 2, maxRow);

                for (int dir = 0; dir < 4; dir++) {
                    int nx = cur.x + dx[dir];
                    int ny = cur.y + dy[dir];

                    if (inRange(nx, ny) && !visited[nx][ny]) {
                        // 현재 위치가 출구이다.
                        if (golems[curGolemIdx].d == dir) {
                            // 그런데 다른 골렘이 있다
                            if (board[nx][ny] > 0) {
                                q.add(new Point(nx, ny));
                                visited[nx][ny] = true;
                            }
                        } else {
                            // 출구가 아닌 경우
                            if (board[nx][ny] == curGolemIdx) {
                                q.add(new Point(nx, ny));
                                visited[nx][ny] = true;
                            }
                        }
                    }
                }

            }

            return maxRow;
        }
    }

    private static class Point {

        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point next(int dir) {
            return new Point(x + dx[dir], y + dy[dir]);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}