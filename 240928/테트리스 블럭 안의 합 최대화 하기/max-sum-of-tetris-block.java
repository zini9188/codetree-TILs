import java.io.*;
import java.util.StringTokenizer;

public class Main {

    private static int[][] map;
    private static int M, N;
    private static int[] dx = {0, 0, 1, -1}, dy = {1, -1, 0, 0};
    private static int answer;
    private static boolean[][] visited;

    public static void main(String[] args) throws IOException {
        input();
        simulation();
    }

    private static void simulation() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                visited[i][j] = true;
                makeBlock(0, map[i][j], i, j);
                visited[i][j] = false;
            }
        }
        System.out.println(answer);
    }

    private static void makeBlock(int depth, int score, int x, int y) {
        if (depth == 3) {
            answer = Math.max(answer, score);
            return;
        }

        if (depth == 1) {
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (outRange(nx, ny) || visited[nx][ny]) {
                    continue;
                }

                visited[nx][ny] = true;

                for (int j = 0; j < 4; j++) {
                    int nx2 = x + dx[j];
                    int ny2 = y + dy[j];

                    if (outRange(nx2, ny2) || visited[nx2][ny2]) {
                        continue;
                    }

                    visited[nx2][ny2] = true;
                    makeBlock(depth + 2, score + map[nx][ny] + map[nx2][ny2], nx, ny);
                    visited[nx2][ny2] = false;
                }
                makeBlock(depth + 1, score + map[nx][ny], nx, ny);

                visited[nx][ny] = false;
            }


        } else {
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (outRange(nx, ny) || visited[nx][ny]) {
                    continue;
                }

                visited[nx][ny] = true;
                makeBlock(depth + 1, score + map[nx][ny], nx, ny);
                visited[nx][ny] = false;
            }
        }
    }

    private static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= M;
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        map = new int[N][M];
        visited = new boolean[N][M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
    }
}