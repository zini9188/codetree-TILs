import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int n, m, k;
    static int[][] map;
    static final int EAST = 0, NORTH = 1, WEST = 2, SOUTH = 3;
    static int[] dx = {0, -1, 0, 1}, dy = {1, 0, -1, 0};
    static Team[] teams;
    static Ball ball;
    static int ans;

    public static void main(String[] args) throws Exception {
        init();
        simulate();
    }

    private static void simulate() {
        for (int i = 1; i <= k; i++) {
            for (int j = 0; j < m; j++) {
                teams[j].move();
            }
            int n = moveBall(i);

            if (n >= 1) {
                ans += (int) Math.pow(n, 2);
            }
        }
        System.out.println(ans);
    }

    private static int moveBall(int round) {
        if (round > n && (round - 1) % n == 0) {
            ball.rotate();
        }

        round = (round - 1) % n;
        for (int i = 0; i < n; i++) {
            int move;
            if (ball.dir == EAST) {
                move = ball.move(round, i);
            } else if (ball.dir == NORTH) {
                move = ball.move(n - i - 1, round);
            } else if (ball.dir == WEST) {
                move = ball.move(round, n - i - 1);
            } else {
                move = ball.move(i, round);
            }

            if (move > 0) {
                int score = teams[move / 1000].score(move % 1000);
                teams[move / 1000].reverse();
                return score;
            }
        }
        return -1;
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        List<int[]> heads = new ArrayList<>();
        // 맵 초기화
        map = new int[n][n];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());

            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());

                if (map[i][j] == 4) {
                    map[i][j] = -1;
                }

                if (map[i][j] == 1) {
                    heads.add(new int[]{i, j});
                }
            }
        }
        // m개의 팀
        teams = new Team[m];
        for (int i = 0; i < m; i++) {
            teams[i] = new Team();
        }
        initTeam(heads);
        ball = new Ball();
    }

    private static void initTeam(List<int[]> heads) {
        Queue<Person> q;
        int teamIndex = 0;
        boolean[][] visited = new boolean[n][n];
        for (int[] head : heads) {
            q = new ArrayDeque<>();
            // 초기 머리의 위치
            Person headPerson = new Person(head[0], head[1], teamIndex * 1000 + 1);
            q.add(headPerson);
            map[head[0]][head[1]] = teamIndex * 1000 + 1;
            visited[head[0]][head[1]] = true;
            teams[teamIndex].people.add(headPerson);
            teams[teamIndex].head = headPerson;
            while (!q.isEmpty()) {
                Person cur = q.poll();

                for (int i = 0; i < 4; i++) {
                    int nx = cur.x + dx[i];
                    int ny = cur.y + dy[i];
                    if (outRange(nx, ny) || map[nx][ny] <= 0
                            || cur.idx == teamIndex * 1000 + 1 && map[nx][ny] == 3
                            || visited[nx][ny]) {
                        continue;
                    }

                    Person person = new Person(nx, ny, cur.idx + 1);
                    if (map[nx][ny] == 3) {
                        teams[teamIndex].tail = person;
                    }

                    map[nx][ny] = cur.idx + 1;
                    teams[teamIndex].people.add(person);
                    q.add(person);
                    visited[nx][ny] = true;
                }
            }
            teamIndex++;
        }
    }

    static void print() {
        System.out.println(Arrays.deepToString(map).replaceAll("],", "\n").replaceAll("-1", "-")
                .replaceAll("100", "").replaceAll("200", "").replaceAll("300", ""));
    }

    private static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= n || y >= n;
    }

    static class Person {

        int x, y, idx;

        public Person(int x, int y, int idx) {
            this.x = x;
            this.y = y;
            this.idx = idx;
        }

        public void move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Person{" + "x=" + x + ", y=" + y + ", idx=" + idx + '}';
        }
    }

    static class Team {

        Person head, tail;
        List<Person> people;
        boolean isReverse;

        public Team() {
            people = new ArrayList<>();
            isReverse = false;
        }

        public void move() {
            map[tail.x][tail.y] = -1;
            if (isReverse) {
                for (int i = 0; i < people.size() - 1; i++) {
                    Person next = people.get(i + 1);
                    people.get(i).move(next.x, next.y);
                    map[next.x][next.y] = next.idx - 1;
                }
            } else {
                for (int i = people.size() - 1; i > 0; i--) {
                    Person next = people.get(i - 1);
                    people.get(i).move(next.x, next.y);
                    map[next.x][next.y] = next.idx + 1;

                    if (i == people.size() - 1) {
                        tail.move(next.x, next.y);
                    }
                }

            }

            for (int i = 0; i < 4; i++) {
                int nx = head.x + dx[i];
                int ny = head.y + dy[i];

                if (outRange(nx, ny)) {
                    continue;
                }

                if (map[nx][ny] == -1) {
                    map[nx][ny] = head.idx;
                    head.move(nx, ny);
                    break;
                }
            }
        }

        public int score(int n) {
            return Math.abs(n - (head.idx % 1000) + 1);
        }

        public void reverse() {
            Person temp = tail;
            tail = head;
            head = temp;
            isReverse = !isReverse;
        }

        @Override
        public String toString() {
            return "Team{" + "head=" + head + ", tail=" + tail + ", people=" + people
                    + ", isReverse=" + isReverse + '}';
        }
    }

    static class Ball {

        int round;
        int dir;

        public Ball() {
            round = 1;
            dir = 0;
        }

        public int move(int x, int y) {
            while (!outRange(x, y)) {
                if (map[x][y] > 0) {
                    return map[x][y];
                }

                x += dx[dir];
                y += dy[dir];
            }

            return -1;
        }

        public void rotate() {
            dir = (dir + 1) % 4;
        }
    }
}