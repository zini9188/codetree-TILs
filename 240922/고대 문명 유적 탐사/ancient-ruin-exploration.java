import java.io.*;
import java.util.*;

public class Main {


    private static int[][] piece;
    private static int M, K;
    private static Queue<Integer> nextValues;
    private static int[] dx = new int[]{-1, -1, -1, 0, 1, 1, 1, 0}, dy = new int[]{-1, 0, 1, 1, 1,
            0, -1, -1}, dx2 = new int[]{-1, 0, 1, 0}, dy2 = new int[]{0, -1, 0, 1};

    public static void main(String[] args) throws IOException {
        input();
        for (int i = 0; i < K; i++) {
            if (!simulation()) {
                break;
            }
        }
    }

    private static boolean simulation() {
        // 중심 선택
        int maxValue = 0;
        int sx = 0;
        int sy = 0;
        int rotate = 0;
        for (int i = 4; i >= 2; i--) {
            for (int j = 4; j >= 2; j--) {
                for (int k = 3; k >= 1; k--) {
                    int value = rotate(i, j, k);
                    if (value > maxValue) {
                        sx = i;
                        sy = j;
                        rotate = k;
                        maxValue = value;
                    }
                }
            }
        }

        if (maxValue == 0) {
            return false;
        }

        int res = rotate2(sx, sy, rotate);
        System.out.print(res + " ");

        return true;
    }

    private static int rotate2(int x, int y, int rotate) {
        Queue<Integer> q = new ArrayDeque<>();

        for (int i = 0; i < 8; i++) {
            q.add(piece[x + dx[i]][y + dy[i]]);
        }

        int rDir = 2 * rotate;
        for (int i = 0; i < 8; i++) {
            piece[x + dx[rDir]][y + dy[rDir]] = q.poll();
            rDir = (rDir + 1) % 8;
        }

        int total = 0;
        while (true) {
            int cnt = 0;
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 5; j++) {
                    if (piece[i][j] == 0) {
                        continue;
                    }

                    int res = bfs2(i, j, piece);
                    if (res >= 3) {
                        cnt += res;
                    }
                }
            }

            total += cnt;

            for (int i = 1; i <= 5; i++) {
                for (int j = 5; j > 0; j--) {
                    if (piece[j][i] == 0) {
                        piece[j][i] = nextValues.poll();
                    }
                }
            }
            if (cnt == 0) {
                break;
            }
        }

        return total;
    }


    private static int bfs2(int x, int y, int[][] temp) {
        Queue<Point> q = new ArrayDeque<>();
        Queue<Point> remove = new ArrayDeque<>();
        q.add(new Point(x, y));
        remove.add(new Point(x, y));
        int value = temp[x][y];
        int cnt = 1;
        boolean[][] visited = new boolean[6][6];
        visited[x][y] = true;

        while (!q.isEmpty()) {
            Point cur = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dx2[i];
                int ny = cur.y + dy2[i];
                if (nx >= 1 && nx <= 5 && ny >= 1 && ny <= 5) {
                    if (visited[nx][ny]) {
                        continue;
                    }

                    if (temp[nx][ny] == value) {
                        cnt++;
                        q.add(new Point(nx, ny));
                        remove.add(new Point(nx, ny));
                        visited[nx][ny] = true;
                    }
                }
            }
        }

        if (remove.size() >= 3) {
            while (!remove.isEmpty()) {
                Point cur = remove.poll();
                temp[cur.x][cur.y] = 0;
            }
        }

        return cnt;
    }

    private static int rotate(int x, int y, int rotate) {
        int[][] temp = new int[6][6];

        for (int i = 1; i <= 5; i++) {
            System.arraycopy(piece[i], 1, temp[i], 1, 5);
        }

        Queue<Integer> q = new ArrayDeque<>();

        for (int i = 0; i < 8; i++) {
            q.add(temp[x + dx[i]][y + dy[i]]);
        }

        int rDir = 2 * rotate;
        for (int i = 0; i < 8; i++) {
            temp[x + dx[rDir]][y + dy[rDir]] = q.poll();
            rDir = (rDir + 1) % 8;
        }

        int cnt = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                if (temp[i][j] == 0) {
                    continue;
                }

                int res = bfs(i, j, temp);
                if (res >= 3) {
                    cnt += res;
                }
            }
        }

        return cnt;
    }

    private static int bfs(int x, int y, int[][] temp) {
        Queue<Point> q = new ArrayDeque<>();
        q.add(new Point(x, y));
        int value = temp[x][y];
        temp[x][y] = 0;
        int cnt = 1;

        while (!q.isEmpty()) {
            Point cur = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dx2[i];
                int ny = cur.y + dy2[i];
                if (nx >= 1 && nx <= 5 && ny >= 1 && ny <= 5) {
                    if (temp[nx][ny] == value) {
                        temp[nx][ny] = 0;
                        cnt++;
                        q.add(new Point(nx, ny));
                    }
                }
            }
        }

        return cnt;
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        piece = new int[6][6];
        for (int i = 1; i <= 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= 5; j++) {
                piece[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        nextValues = new ArrayDeque<>();
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            nextValues.add(Integer.parseInt(st.nextToken()));
        }
    }

    private static class Point {

        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}