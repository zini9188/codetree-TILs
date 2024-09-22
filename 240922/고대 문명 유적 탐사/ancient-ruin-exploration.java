import java.io.*;
import java.util.*;

public class Main {

    private static int[][] originPieces;
    private static Queue<Integer> newValues;
    private static int K, M;

    private static int[] dx = new int[]{-1, -1, -1, 0, 1, 1, 1, 0},
            dy = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
    private static int[] dx2 = {-1, 1, 0, 0}, dy2 = {0, 0, 1, -1};

    public static void main(String[] args) throws IOException {
        // 유물
        // 1. 격자 회전, 90 180 270
        // 2. 가장 큰 회전 방법 찾기
        // 3. 찾은 회전 방법으로 회전하고 유물 획득
        // 4. 유물을 획득하면 빈 공간에 유물을 채워넣음
        // 5. 채워넣었을때 유물 획득 가능하면 다시 4.
        // 6. K번 반복

        input();
        solution();
    }

    private static void solution() {
        // K번 반복
        for (int i = 0; i < K; i++) {
            // 회전 격자를 찾음.
            int[] selectInfo = selectPieces();
            // 가장 점수가 큰 회전 정보를 바탕으로 실제 배열 회전
            if (selectInfo == null) {
                break;
            }

            rotate(selectInfo[0], selectInfo[1], selectInfo[2], originPieces);
            // 회전한 배열에서 유물 삭제
            System.out.print(detectPiece() + " ");
        }
    }

    private static int detectPiece() {
        int totalCnt = 0;

        while (true) {
            boolean[][] visited = new boolean[6][6];
            Stack<int[]> delete = new Stack<>();
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 5; j++) {
                    if (visited[i][j]) {
                        continue;
                    }

                    Queue<int[]> q = new ArrayDeque<>();
                    q.add(new int[]{i, j});
                    delete.add(new int[]{i, j});
                    visited[i][j] = true;
                    int cnt = 1;
                    while (!q.isEmpty()) {
                        int[] cur = q.poll();

                        for (int dir = 0; dir < 4; dir++) {
                            int nx = cur[0] + dx2[dir];
                            int ny = cur[1] + dy2[dir];

                            if (nx >= 1 && nx <= 5 && ny >= 1 && ny <= 5 && !visited[nx][ny]
                                    && originPieces[nx][ny] == originPieces[i][j]) {
                                visited[nx][ny] = true;
                                q.add(new int[]{nx, ny});
                                delete.add(new int[]{nx, ny});
                                cnt++;
                            }
                        }
                    }

                    if (cnt < 3) {
                        for (int k = 0; k < cnt; k++) {
                            delete.pop();
                        }
                    }
                }
            }

            // 유물 없으면 끝
            if (delete.isEmpty()) {
                return totalCnt;
            }

            // 유물 있으면 총 개수에 더해줌
            totalCnt += delete.size();
            while (!delete.isEmpty()) {
                int[] cur = delete.pop();
                originPieces[cur[0]][cur[1]] = 0;
            }

            // 삭제된 곳에 유물 채워넣음
            for (int i = 1; i <= 5; i++) {
                for (int j = 5; j > 0; j--) {
                    if (originPieces[j][i] == 0) {
                        originPieces[j][i] = newValues.poll();
                    }
                }
            }
        }
    }

    private static int[][] copy(int[][] origin) {
        int[][] temp = new int[6][6];
        // 배열 복사
        for (int i = 0; i < 6; i++) {
            System.arraycopy(origin[i], 0, temp[i], 0, 6);
        }
        return temp;
    }

    private static int[] selectPieces() {
        // 좌표, 회전 정보
        int[] info = new int[3];
        int maxCnt = 0;
        // 격자는 행이 작고, 열이 작아야 함.
        for (int i = 4; i > 1; i--) {
            for (int j = 4; j > 1; j--) {
                for (int k = 3; k >= 1; k--) {
                    // 회전 배열 가져옴
                    int[][] temp = copy(originPieces);
                    rotate(i, j, k, temp);

                    // 획득 가능한 유물 개수 파악
                    int cnt = detectPieceCount(temp);

                    if (cnt > maxCnt) {
                        info[0] = i;
                        info[1] = j;
                        info[2] = k;
                        maxCnt = cnt;
                    }
                }
            }
        }

        if (maxCnt == 0) {
            return null;
        }

        return info;
    }

    private static int detectPieceCount(int[][] temp) {
        boolean[][] visited = new boolean[6][6];
        Queue<int[]> q = new ArrayDeque<>();
        int totalCnt = 0;
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                if (!visited[i][j]) {
                    int cnt = 1;
                    visited[i][j] = true;
                    q.add(new int[]{i, j});
                    while (!q.isEmpty()) {
                        int[] cur = q.poll();

                        for (int dir = 0; dir < 4; dir++) {
                            int nx = cur[0] + dx2[dir];
                            int ny = cur[1] + dy2[dir];

                            // 범위 검사
                            if (nx >= 1 && nx <= 5 && ny >= 1 && ny <= 5) {
                                if (!visited[nx][ny] && temp[nx][ny] == temp[i][j]) {
                                    visited[nx][ny] = true;
                                    cnt++;
                                    q.add(new int[]{nx, ny});
                                }
                            }
                        }
                    }

                    if (cnt >= 3) {
                        totalCnt += cnt;
                    }
                }
            }
        }

        return totalCnt;
    }


    // 회전한 복사 배열
    private static void rotate(int x, int y, int rotate, int[][] arr) {
        int[][] copy = copy(originPieces);
        int nDir = 2 * rotate;
        for (int i = 0; i < 8; i++) {
            arr[x + dx[nDir]][y + dy[nDir]] = copy[x + dx[i]][y + dy[i]];
            nDir = (nDir + 1) % 8;
        }
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        // 초기 배열 세팅
        originPieces = new int[6][6];
        for (int i = 1; i <= 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= 5; j++) {
                originPieces[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 새로운 유물 조각 가치들
        newValues = new ArrayDeque<>();
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            newValues.add(Integer.valueOf(st.nextToken()));
        }
    }
}