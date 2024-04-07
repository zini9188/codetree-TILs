import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int N, M;
    static char[][] box;
    static Queue<Pair> q;
    static int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
    static boolean[][][][] visited;


    public static void main(String[] args) throws IOException {
        // N * N 크기의 상자
        // 파란색 사탕, 빨간색 사탕
        // 상자 안에는 사탕 못지나가게하는 장애물, 구멍하나

        // 빨간색 사탕을 빼내려함
        // 상자는 상하좌우로 기울일 수 있음
        // 기울어진 방향으로 사탕은 다른 사탕, 장애물에 부딪히기 전까지 미끌어짐
        // 파란색이 나오면 안됨
        //

        init();
        int ans = simulate();
        System.out.println(ans);
    }

    private static int simulate() {
        for (int i = 1; i <= 10; i++) {
            int size = q.size();
            for (int j = 0; j < size; j++) {
                Pair cur = q.poll();
                for (int k = 0; k < 4; k++) {
                    Candy blue = cur.blue;
                    Candy red = cur.red;

                    // 파랑색 캔디 해당 방향으로 최대한 옮김
                    Candy blueInfo = getInfo(k, blue);

                    // 빨간색 캔디 해당 방향으로 최대한 옮김
                    Candy redInfo = getInfo(k, red);

                    // 둘 다 못움직이면
                    if (blueInfo.m == 0 && redInfo.m == 0) {
                        continue;
                    }

                    // 만약 빨강이 탈출구면
                    if (box[redInfo.x][redInfo.y] == 'O') {
                        // 파랑도 왔는지 검사함
                        if (box[blueInfo.x][blueInfo.y] != 'O') {
                            return i;
                        }
                    }
                    // 파랑이 탈출구 아닐때만 동작
                    else if (box[blueInfo.x][blueInfo.y] != 'O') {
                        // 둘이 같은 위치인 경우
                        if (blueInfo.x == redInfo.x && blueInfo.y == redInfo.y) {
                            // 파랑이 더 많이 움직였다면 한 칸 뒤로 가줌
                            if (blueInfo.m > redInfo.m) {
                                blueInfo.x -= dx[k];
                                blueInfo.y -= dy[k];
                            }
                            // 빨강이 더 많이 움직였다면 한 칸 뒤로 가줌
                            else {
                                redInfo.x -= dx[k];
                                redInfo.y -= dy[k];
                            }
                        }

                        if (!visited[blueInfo.x][blueInfo.y][redInfo.x][redInfo.y]) {
                            q.add(new Pair(new Candy(blueInfo.x, blueInfo.y),
                                    new Candy(redInfo.x, redInfo.y)));
                            visited[blueInfo.x][blueInfo.y][redInfo.x][redInfo.y] = true;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private static Candy getInfo(int k, Candy blue) {
        // 파랑색 캔디 해당 방향으로 최대한 옮김
        int nx = blue.x;
        int ny = blue.y;
        int nm = 0;
        while (box[nx + dx[k]][ny + dy[k]] != '#') {
            nx += dx[k];
            ny += dy[k];
            nm++;

            if (box[nx][ny] == 'O') {
                break;
            }
        }

        return new Candy(nx, ny, nm);
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        box = new char[N][M];

        Candy blue = null;
        Candy red = null;
        for (int i = 0; i < N; i++) {
            box[i] = br.readLine().toCharArray();
            for (int j = 0; j < M; j++) {
                if (box[i][j] == 'B') {
                    blue = new Candy(i, j);
                }

                if (box[i][j] == 'R') {
                    red = new Candy(i, j);
                }
            }
        }
        visited = new boolean[N][M][N][M];
        q = new ArrayDeque<>();
        q.add(new Pair(blue, red));
    }


    static class Candy {

        int x, y, m;

        public Candy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Candy(int x, int y, int m) {
            this.x = x;
            this.y = y;
            this.m = m;
        }
    }

    static class Pair {

        Candy blue, red;

        public Pair(Candy blue, Candy red) {
            this.blue = blue;
            this.red = red;
        }
    }
}