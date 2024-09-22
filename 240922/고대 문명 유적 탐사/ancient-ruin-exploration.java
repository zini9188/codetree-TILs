import java.io.*;
import java.util.*;

public class Main {

    private static Queue<Integer> newValues;
    private static int K, M;
    private static int[] dx = new int[]{-1, -1, -1, 0, 1, 1, 1, 0},
            dy = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
    private static int[] dx2 = {-1, 1, 0, 0}, dy2 = {0, 0, 1, -1};
    private static Square origin;

    public static void main(String[] args) throws IOException {
        input();
        solution();
    }

    private static void solution() {
        // K번 반복
        for (int repeat = 0; repeat < K; repeat++) {
            // 회전 격자를 찾음.
            int maxScore = 0;

            Square maxSquare = null;
            for (int k = 1; k <= 3; k++) {
                for (int j = 1; j <= 3; j++) {
                    for (int i = 1; i <= 3; i++) {
                        Square rotate = origin.rotate(i, j, k);
                        int score = rotate.calcScore();

                        if (score > maxScore) {
                            maxScore = score;
                            maxSquare = rotate;
                        }
                    }
                }
            }

            if (maxSquare == null) {
                break;
            }

            origin = maxSquare;

            // 회전한 배열에서 유물 삭제
            while (true) {
                origin.fill();
                int res = origin.calcScore();
                if (res == 0) {
                    break;
                }
                maxScore += res;
            }

            System.out.print(maxScore + " ");
        }
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        // 초기 배열 세팅
        origin = new Square();
        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                origin.arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 새로운 유물 조각 가치들
        newValues = new ArrayDeque<>();
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            newValues.add(Integer.valueOf(st.nextToken()));
        }
    }

    public static class Square {

        int[][] arr;

        public Square() {
            arr = new int[5][5];
        }

        public Square rotate(int x, int y, int rotate) {
            Square square = new Square();

            for (int i = 0; i < 5; i++) {
                System.arraycopy(arr[i], 0, square.arr[i], 0, 5);
            }

            int nDir = 2 * rotate;
            for (int i = 0; i < 8; i++) {
                square.arr[x + dx[nDir]][y + dy[nDir]] = arr[x + dx[i]][y + dy[i]];
                nDir = (nDir + 1) % 8;
            }

            return square;
        }

        public void fill() {
            for (int i = 0; i < 5; i++) {
                for (int j = 4; j >= 0; j--) {
                    if (arr[j][i] == 0) {
                        arr[j][i] = newValues.poll();
                    }
                }
            }
        }

        public int calcScore() {
            int score = 0;
            boolean[][] visited = new boolean[5][5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (visited[i][j]) {
                        continue;
                    }

                    Queue<int[]> q = new ArrayDeque<>();
                    Queue<int[]> d = new ArrayDeque<>();
                    q.add(new int[]{i, j});
                    d.add(new int[]{i, j});
                    visited[i][j] = true;
                    int cnt = 1;
                    while (!q.isEmpty()) {
                        int[] cur = q.poll();

                        for (int dir = 0; dir < 4; dir++) {
                            int nx = cur[0] + dx2[dir];
                            int ny = cur[1] + dy2[dir];

                            if (inRange(nx, ny) && !visited[nx][ny]
                                    && arr[nx][ny] == arr[i][j]) {
                                visited[nx][ny] = true;
                                q.add(new int[]{nx, ny});
                                d.add(new int[]{nx, ny});
                                cnt++;
                            }
                        }
                    }

                    if (cnt >= 3) {
                        score += d.size();
                        while (!d.isEmpty()) {
                            int[] p = d.poll();
                            arr[p[0]][p[1]] = 0;
                        }
                    }
                }
            }
            return score;
        }

        private boolean inRange(int x, int y) {
            return x >= 0 && x < 5 && y >= 0 && y < 5;
        }
    }
}