# springboot-mesos-chronos

The goal of this project is to create a simple [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Java application called `simple-service` and use [`Mesos`](http://mesos.apache.org) / [`Chronos`](https://mesos.github.io/chronos) to run it in specific intervals.

## Application

- ### simple-service

  It's a dummy and simple `Spring Boot` Java application. In order to simulate the finishing status of the application successfully or with an error, there is an environment variable `EXIT_CODE`. Besides, there is another environment variable called `SLEEP`. It can be used to change the amount of time the thread sleeps (in milliseconds). The idea of the `SLEEP` is to simulate the application processing time.

  The table below summarizes the environment variables.

  | Env. variable | Values                    | Default | Description                                                            |
  | ------------- | ------------------------- | ------- | ---------------------------------------------------------------------- |
  | `EXIT_CODE`   | 0 = success; !0 = failure | 0       | For simulating the finishing status of the application                 |
  | `SLEEP`       | integer > 0               | 5000    | For simulating the application processing time (value in milliseconds) |

## Prerequisites

- [`Java 11+`](https://www.oracle.com/java/technologies/downloads/#java11)
- [`Docker`](https://www.docker.com/)
- [`Docker-Compose`](https://docs.docker.com/compose/install/)

## Warning: Mac Users

   After some recent `Docker Desktop` updates, we need to add a new directory called `/var/lib` to Docker `File Sharing` resources, otherwise we will see an exception like
   ```
   docker: Error response from daemon: Mounts denied: 
   The path /var/lib/mesos/slaves/347adc13-aad3-4c25-9864-ee2ebcc97572-S0/frameworks/347adc13-aad3-4c25-9864-ee2ebcc97572-0000/executors/keycloak.05642772-1ae3-11eb-aecf-0242ac120007/runs/33e0751e-1a0a-40d8-b9f8-cfa706679172
   is not shared from OS X and is not known to Docker.
   You can configure shared paths from Docker -> Preferences... -> File Sharing.
   See https://docs.docker.com/docker-for-mac/osxfs/#namespaces for more info.
   ```

   Unfortunately, it's not possible to do it by using `Docker Desktop` UI. So, we need to do it manually by following the next steps:
   - Open `~/Library/Group\ Containers/group.com.docker/settings.json` using your favorite editor;
   - Look for `filesharingDirectories`. It should look like
     ```
     "filesharingDirectories": [
       "/Users",
       "/Volumes",
       "/private",
       "/tmp",
       "/var/folders"
     ],
     ```
   - Append the following line:
     ```
     "/var/lib"
     ```
   - The new array should now look like the one below (mind the comma after `"/var/folders"`):
     ```
     "filesharingDirectories": [
       "/Users",
       "/Volumes",
       "/private",
       "/tmp",
       "/var/folders",
       "/var/lib"
     ],
     ```
   - Save the file and exit
   - Restart `Docker Desktop`

## Start Environment

- Open a terminal and make sure you are in `springboot-mesos-chronos` root folder

- Export to an environment variable called `HOST_IP` the machine ip address
  ```
  export HOST_IP=$(ipconfig getifaddr en0)
  ```

- Run the following command
  ```
  docker-compose up -d
  ```

- Wait for Docker containers to be up and running. To check it, run
  ```
  docker-compose ps
  ```

## Service's URL

| Service | URL                   |
| ------- | --------------------- |
| Mesos   | http://localhost:5050 |
| Chronos | http://localhost:4400 |

## Build Docker Image

- In a terminal, make sure you are inside `springboot-mesos-chronos` root folder

- Run the following command
  ```
  ./mvnw clean compile jib:dockerBuild --projects simple-service
  ```

- You can check the application and Docker image by running
  ```
  docker run --rm --name simple-service \
    -e EXIT_CODE=0 -e SLEEP=1000 \
    ivanfranchin/simple-service:1.0.0; echo $?
  ```

## Running as a Chronos Job

- Edit some properties present in `chronos/simple-service.json`. For example, change the `schedule` to a specific date/time (UTC) in the future.

- In a terminal and, inside `springboot-mesos-chronos` root folder, run the `curl` command below to add jobs to `Chronos`.
  ```
  curl -i -X POST \
    -H "Content-Type: application/json" \
    -d@./chronos/simple-service.json \
    http://localhost:4400/v1/scheduler/iso8601
  ```
  > **Note:** For more about `Chronos` endpoints visit https://mesos.github.io/chronos/docs/api.html

- You can check and edit the schedule of the jobs by visiting [`Chronos` website](http://localhost:4400).

  ![chronos](documentation/chronos.png)

- To check the history of complete tasks, stderr and stdout of those tasks, etc, visit [`Mesos` website](http://localhost:5050).

  ![mesos](documentation/mesos.png)

## Shutdown

- In a terminal, make sure you are inside `springboot-mesos-chronos` root folder

- To stop and remove docker-compose containers, network and volumes, run
  ```
  docker-compose down -v
  docker rm -v $(docker ps -a -f status=exited -f status=created -q)
  ```

## Cleanup

- To remove the Docker image created in this project, go to a terminal and run the command below
  ```
  docker rmi ivanfranchin/simple-service:1.0.0
  ```

- **Mac Users**

  Undo changes in `~/Library/Group\ Containers/group.com.docker/settings.json` file
  - Open `~/Library/Group\ Containers/group.com.docker/settings.json` using your favorite editor
  - Remove `"/var/lib"` of the `filesharingDirectories` array present at the top of file
  - Restart `Docker Desktop`