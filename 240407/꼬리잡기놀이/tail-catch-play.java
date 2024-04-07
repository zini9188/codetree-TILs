import java.io.*;
import java.util.*;

public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static int n, m, k;
    static int[][] map;
    // 우 상 좌 하
    static int[] dx = {0, -1, 0, 1}, dy = {1, 0, -1, 0};
    static Team[] teams;
    static int[] personTeam;
    static Ball ball;
    static int ans = 0;

    public static void main(String[] args) throws IOException {
        init();
        simulate();
        System.out.println(ans);
    }

    private static void simulate() {
        for (int i = 1; i <= k; i++) {
            moveTeams();
            int score = ball.move(i);

            if (score > 1) {
                ans += Math.pow(score, 2);
            }

            if (i % 4 == 0) {
                ball.rotate();
            }
        }
    }

    private static void moveTeams() {
        for (int i = 1; i <= m; i++) {
            teams[i].move();
        }

        print();
    }

    private static void init() throws IOException {
        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        map = new int[n][n];
        personTeam = new int[n * n + 1];
        ball = new Ball();
        List<Person> p = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if (map[i][j] == 4) {
                    map[i][j] = -1;
                }

                if (map[i][j] == 1) {
                    p.add(new Person(0, i, j));
                }
            }
        }

        teams = new Team[m + 1];
        for (int i = 0; i < m + 1; i++) {
            teams[i] = new Team();
        }

        boolean[][] visited = new boolean[n][n];
        int teamIndex = 1;
        int personIndex = 1;
        Queue<Person> q;
        for (Person person : p) {
            q = new ArrayDeque<>();
            person.idx = personIndex;
            q.add(person);
            visited[person.x][person.y] = true;
            teams[teamIndex].people.add(person);
            personTeam[personIndex] = teamIndex;
            map[person.x][person.y] = personIndex++;
            while (!q.isEmpty()) {
                Person cur = q.poll();

                for (int i = 0; i < 4; i++) {
                    int nx = cur.x + dx[i];
                    int ny = cur.y + dy[i];

                    if (outRange(nx, ny) || visited[nx][ny] || map[nx][ny] == 0
                            || map[nx][ny] == -1) {
                        continue;
                    }

                    visited[nx][ny] = true;
                    map[nx][ny] = personIndex;
                    personTeam[personIndex] = teamIndex;
                    Person np = new Person(personIndex++, nx, ny);
                    teams[teamIndex].people.add(np);
                    q.add(np);
                }
            }
            teamIndex++;
        }

        print();
    }

    private static void print() {
//        System.out.println(Arrays.toString(personTeam));
//        for (Team team : teams) {
//            System.out.println(team);
//        }
//        System.out.println(Arrays.deepToString(map).replaceAll("],", "\n").replaceAll("-1", "-"));
    }

    private static boolean outRange(int x, int y) {
        return x < 0 || y < 0 || x >= n || y >= n;
    }

    static class Person {

        int idx, x, y;

        public Person(int idx, int x, int y) {
            this.idx = idx;
            this.x = x;
            this.y = y;
        }

        public Person(Person front) {
            this.idx = front.idx + 1;
            this.x = front.x;
            this.y = front.y;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "idx=" + idx +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }

        public void move(int nx, int ny) {
            map[nx][ny] = map[x][y];
            x = nx;
            y = ny;
        }
    }

    static class Team {

        List<Person> people;
        boolean flag;


        public Team() {
            people = new ArrayList<>();
            flag = false;
        }

        public void move() {
            // 역방향
            if (flag) {
                // 머리 사람 움직임
                Person front = people.get(people.size() - 1);
                Person temp = new Person(front);
                for (int i = 0; i < 4; i++) {
                    int nx = front.x + dx[i];
                    int ny = front.y + dy[i];

                    if (outRange(nx, ny)) {
                        continue;
                    }

                    if (map[nx][ny] == -1) {
                        front.move(nx, ny);
                        break;
                    }
                }

                for (int i = people.size() - 2; i > 0; i--) {
                    // 현재 사람 복사
                    Person t = new Person(people.get(i));
                    // 다음 좌표로 현재 사람 인덱스 복사
                    map[temp.x][temp.y] = temp.idx;
                    // 현재 사람 바꾸기
                    people.set(i, temp);
                    temp = t;
                }

                Person tail = new Person(people.get(0));
                map[temp.x][temp.y] = temp.idx;
                people.set(0, temp);
                map[tail.x][tail.y] = -1;
            }
            // 정방향
            else {
                // 머리 사람 움직임
                Person front = people.get(0);
                Person temp = new Person(front);
                for (int i = 0; i < 4; i++) {
                    int nx = front.x + dx[i];
                    int ny = front.y + dy[i];

                    if (outRange(nx, ny)) {
                        continue;
                    }

                    if (map[nx][ny] == -1) {
                        front.move(nx, ny);
                        break;
                    }
                }

                for (int i = 1; i < people.size() - 1; i++) {
                    // 현재 사람 복사
                    Person t = new Person(people.get(i));
                    // 다음 좌표로 현재 사람 인덱스 복사
                    map[temp.x][temp.y] = temp.idx;
                    // 현재 사람 바꾸기
                    people.set(i, temp);
                    temp = t;
                }

                Person tail = new Person(people.get(people.size() - 1));
                people.set(people.size() - 1, temp);
                map[temp.x][temp.y] = temp.idx;
                map[tail.x][tail.y] = -1;
            }
        }

        @Override
        public String toString() {
            return "Team{" +
                    "people=" + people +
                    ", flag=" + flag +
                    '}';
        }
    }

    // 공은 각 라운드 별로 한 줄 씩 이동한다.
    // n번의 라운드 후 공은 회전한다.
    // 공은 동 북 서 남의 방향으로 회전한다.
    // 공은 맨 처음에 사람을 만나면 해당 팀에게 k^2의 점수를 준다.
    // 아무도 받지 못하면 아무 점수도 얻지 못한다.
    // 공을 획득하면 머리사람과 꼬리사람이 바뀐다.
    static class Ball {

        int dir;

        public Ball() {
            dir = 0;
        }

        // 우 상 좌 하
        public int move(int round) {
            if (dir == 0) {
                for (int i = 0; i < n; i++) {
                    // 사람이 있는 경우
                    int value = map[round - 1][i];
                    if (value > 0) {
                        // 해당 사람의 팀
                        int teamIndex = personTeam[value];
                        for (Person person : teams[teamIndex].people) {
                            // 해당 사람의 팀에서 값 찾음
                            if (person.idx == value) {
                                // 역방향인 경우
                                Team team = teams[teamIndex];
                                if (team.flag) {
                                    team.flag = false;
                                    return team.people.get(team.people.size() - 1).idx
                                            - person.idx + 1;
                                } else {
                                    team.flag = true;
                                    return person.idx - team.people.get(0).idx + 1;
                                }
                            }
                        }
                    }
                }
            }

            return -1;
        }

        public void rotate() {
            dir = (dir + 1) % 4;
        }
    }
}