일하면서 로컬에서 빌드했을 때와 JAR로 빌드 되었을 때의 파일 구조 차이로 인해 삽질을 경험했기에 적게 되었다.

로컬과 JAR로 패키징 된 환경과 파일 구조가 다르다는 것을 명심하자.
resources 폴더 안에 데이터 탐색 시 아래와 같은 로직을 작성하는 것이 좋다. 또 "classpath:xx" 를 작성해주어야 한다.



public Resource loadEmployeesWithResourceLoader() {
    return resourceLoader.getResource(
      "classpath:data/employees.dat");
}
Jar 파일 경로 탐색 시

jar:file:/

로컬 파일 경로 탐색 시

file:/

때문에, File 은 경로를 찾지 못한다. resourceLoader.getResource("test/test.txt").getFile()접근하지 못하고 FileNotFoundException 에러를 반환한다.

대체 → ClassPathResource("example.txt").inputStream or Resource("example.txt").inputStream 으로 파일을 읽어야 한다.

JAR 파일 구조


example.jar
 |
 +-META-INF
 |  +-MANIFEST.MF
 +-org
 |  +-springframework
 |     +-boot
 |        +-loader
 |           +-<spring boot loader classes>
 +-BOOT-INF
    +-classes
    |  +-mycompany
    |     +-project
    |        +-YourClasses.class
    +-lib
       +-dependency1.jar
       +-dependency2.jar
JAR로 패키징 될 때  resources 파일을 함께 패키징하고 resources 폴더가 없어진다는 것을 명심하자.

때문에, 정적 파일을 읽을 때는 resources 폴더 안에 넣어도 되지만 동적 파일(수정 및 생성)은 외부 폴더를 만들어서 관리 하자.

 

ex) 현재 디렉토리 확인



// Paths 객체 활용
Path currentPath = Paths.get("");
String path = currentPath.toAbsolutePath().toString(); // 현재 디렉토리
System.out.println("현재 작업 경로: " + path);
File file = new File(path);
String[] directories = file.list((dir, name) -> new File(dir, name).isDirectory());
System.out.println(Arrays.toString(directories)); // 현재 경로에서 모든 디렉토리 확인
// System 객체활용
// 현재 디렉토리의 파일 객체 생성
File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());  
 

ex) 현재 디렉토리에서 json 폴더 안으로 이동 후 File 생성 코드



private File getCurrentPathFile(LanguageType languageType) {
    Path currentPath = Paths.get("").resolve("json");
    String path = currentPath.toAbsolutePath() + "/aIPersonaNewsListItem-" + languageType + ".json";
    return new File(path);
}
 

ex)현재 디렉토리에서 File 생성 후 글쓰기



private void writeFileData(Gson gson, File file, List<AIPersonaNewsListItem> aIPersonaNewsListItems) throws IOException {
    String json = gson.toJson(aIPersonaNewsListItems);
    FileWriter fw = new FileWriter(file);
    BufferedWriter writer = new BufferedWriter(fw);
    writer.write(json);
    writer.close();
}
 

ex) 현재 디렉토리에서 File 읽은 후 반환



 private List<AIPersonaNewsListItem> readFileData(Gson gson, File file) throws IOException {
    FileInputStream fIn = new FileInputStream(file);
    BufferedReader br = new BufferedReader(new InputStreamReader(fIn, StandardCharsets.UTF_8));
    String line;
    StringBuilder sb = new StringBuilder();
    while (true) {
        if (!((line = br.readLine()) != null)){
            break;
        } else{
            sb.append(line);
        }
    }
    String json = sb.toString();
    return gson.fromJson(json,new TypeToken<List<AIPersonaNewsListItem>>() {}.getType());
}
 

ex) 현재 디렉토리에서 기존 File 내용 삭제 후 다시 File 생성



private void deleteFileDataBeforeWriteFileData(Gson gson, File file,
                                                List<AIPersonaNewsListItem> aIPersonaNewsListItems) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    fileWriter.write("");
    fileWriter.close();
    String json = gson.toJson(aIPersonaNewsListItems);
    FileWriter fw = new FileWriter(file);
    BufferedWriter writer = new BufferedWriter(fw);
    writer.write(json);
    writer.close();
}
