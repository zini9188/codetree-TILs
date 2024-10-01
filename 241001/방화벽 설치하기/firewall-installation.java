import java.io.*;
import java.util.*;

public class Main {

    private static int N;
    private static int M;
    private static int[][] map;
    private static boolean[][] visited;
    private static List<int[]> fire;
    private static int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
    private static int zero = 0;
    private static int ans = Integer.MIN_VALUE;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        fire = new LinkedList<>();
        map = new int[N][M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());

                if (map[i][j] == 2) {
                    fire.add(new int[]{i, j});
                } else if (map[i][j] == 0) {
                    zero++;
                }
            }
        }

        visited = new boolean[N][M];
        selectFireWall(0);
        System.out.println(ans);
    }

    private static void selectFireWall(int depth) {
        if (depth == 3) {
            bfs();
            return;
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (!visited[i][j] && map[i][j] == 0) {
                    visited[i][j] = true;
                    map[i][j] = 1;
                    selectFireWall(depth + 1);
                    visited[i][j] = false;
                    map[i][j] = 0;
                }
            }
        }
    }

    private static void bfs() {
        int[][] newMap = new int[N][M];
        for (int i = 0; i < N; i++) {
            if (M >= 0) {
                System.arraycopy(map[i], 0, newMap[i], 0, M);
            }
        }

        int cnt = 0;
        Queue<int[]> q = new ArrayDeque<>();
        for (int[] points : fire) {
            q.add(points);
            newMap[points[0]][points[1]] = 2;
            while (!q.isEmpty()) {
                int[] p = q.poll();

                for (int i = 0; i < 4; i++) {
                    int nx = p[0] + dx[i];
                    int ny = p[1] + dy[i];

                    if (outRange(nx, ny) || newMap[nx][ny] != 0) {
                        continue;
                    }

                    q.add(new int[]{nx, ny});
                    cnt++;
                    newMap[nx][ny] = 2;
                }
            }
        }
        ans = Math.max(ans, zero - cnt - 3);
    }

    private static boolean outRange(int nx, int ny) {
        return nx < 0 || ny < 0 || nx >= N || ny >= M;
    }
}