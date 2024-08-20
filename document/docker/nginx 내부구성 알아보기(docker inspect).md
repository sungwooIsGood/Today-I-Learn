nginx의 내부를 알기전에 로컬 환경은

`C:\Users\Ian\Desktop\docker_lab\ex03` 이다. `ex03`디렉토리 안에는 conf,webapp 폴더와 Dockerfile 이 있다.

nginx를 일단 pull 받은 후 컨테이너로 실행시켜 보자.

```docker
docker run -d -p 8080:80 --name nginx-detail nginx
```

![Untitled (2)](https://github.com/user-attachments/assets/993e4a27-0641-4710-9845-29889923228c)

실행중인 컨테이너를 분석하기 위해 `docker inspect` 를 사용하자.

```docker
docker inspect nginx-detail
```

```json
[
    {
        "Id": "c418eb9c0772a9b4143eab53f7e9788cf1a24e6b23bf3edad54085eb99682744",
        "Created": "2023-09-18T00:17:48.367162006Z",
        "Path": "/docker-entrypoint.sh",
        "Args": [
            "nginx",
            "-g",
            "daemon off;"
        ],
        "State": {
            "Status": "running",
            "Running": true,
            "Paused": false,
            "Restarting": false,
            "OOMKilled": false,
            "Dead": false,
            "Pid": 20713,
            "ExitCode": 0,
            "Error": "",
            "StartedAt": "2023-09-18T00:17:51.655915899Z",
            "FinishedAt": "0001-01-01T00:00:00Z"
        },
        "Image": "sha256:f5a6b296b8a29b4e3d89ffa99e4a86309874ae400e82b3d3993f84e1e3bb0eb9",
        "ResolvConfPath": "/var/lib/docker/containers/c418eb9c0772a9b4143eab53f7e9788cf1a24e6b23bf3edad54085eb99682744/resolv.conf",
        "HostnamePath": "/var/lib/docker/containers/c418eb9c0772a9b4143eab53f7e9788cf1a24e6b23bf3edad54085eb99682744/hostname",
        "HostsPath": "/var/lib/docker/containers/c418eb9c0772a9b4143eab53f7e9788cf1a24e6b23bf3edad54085eb99682744/hosts",   
        "LogPath": "/var/lib/docker/containers/c418eb9c0772a9b4143eab53f7e9788cf1a24e6b23bf3edad54085eb99682744/c418eb9c0772a9b4143eab53f7e9788cf1a24e6b23bf3edad54085eb99682744-json.log",
        "Name": "/nginx-detail",
        "RestartCount": 0,
        "Driver": "overlay2",
        "Platform": "linux",
        "MountLabel": "",
        "ProcessLabel": "",
        "AppArmorProfile": "",
        "ExecIDs": null,
        "HostConfig": {
            "Binds": null,
            "ContainerIDFile": "",
            "LogConfig": {
                "Type": "json-file",
                "Config": {}
            },
            "NetworkMode": "default",
            "PortBindings": {
                "80/tcp": [
                    {
                        "HostIp": "",
                        "HostPort": "8080"
                    }
                ]
            },
            "RestartPolicy": {
                "Name": "no",
                "MaximumRetryCount": 0
            },
            "AutoRemove": false,
            "VolumeDriver": "",
            "VolumesFrom": null,
            "ConsoleSize": [
                27,
                225
            ],
            "CapAdd": null,
            "CapDrop": null,
            "CgroupnsMode": "host",
            "Dns": [],
            "DnsOptions": [],
            "DnsSearch": [],
            "ExtraHosts": null,
            "GroupAdd": null,
            "IpcMode": "private",
            "Cgroup": "",
            "Links": null,
            "OomScoreAdj": 0,
            "PidMode": "",
            "Privileged": false,
            "PublishAllPorts": false,
            "ReadonlyRootfs": false,
            "SecurityOpt": null,
            "UTSMode": "",
            "UsernsMode": "",
            "ShmSize": 67108864,
            "Runtime": "runc",
            "Isolation": "",
            "CpuShares": 0,
            "Memory": 0,
            "NanoCpus": 0,
            "CgroupParent": "",
            "BlkioWeight": 0,
            "BlkioWeightDevice": [],
            "BlkioDeviceReadBps": [],
            "BlkioDeviceWriteBps": [],
            "BlkioDeviceReadIOps": [],
            "BlkioDeviceWriteIOps": [],
            "CpuPeriod": 0,
            "CpuQuota": 0,
            "CpuRealtimePeriod": 0,
            "CpuRealtimeRuntime": 0,
            "CpusetCpus": "",
            "CpusetMems": "",
            "Devices": [],
            "DeviceCgroupRules": null,
            "DeviceRequests": null,
            "MemoryReservation": 0,
            "MemorySwap": 0,
            "MemorySwappiness": null,
            "OomKillDisable": false,
            "PidsLimit": null,
            "Ulimits": null,
            "CpuCount": 0,
            "CpuPercent": 0,
            "IOMaximumIOps": 0,
            "IOMaximumBandwidth": 0,
            "MaskedPaths": [
                "/proc/asound",
                "/proc/acpi",
                "/proc/kcore",
                "/proc/keys",
                "/proc/latency_stats",
                "/proc/timer_list",
                "/proc/timer_stats",
                "/proc/sched_debug",
                "/proc/scsi",
                "/sys/firmware"
            ],
            "ReadonlyPaths": [
                "/proc/bus",
                "/proc/fs",
                "/proc/irq",
                "/proc/sys",
                "/proc/sysrq-trigger"
            ]
        },
        "GraphDriver": {
            "Data": {
                "LowerDir": "/var/lib/docker/overlay2/e012e7212d6ab9e050b33b23c15379bf21c4d4358a936db095b00a646753516d-init/diff:/var/lib/docker/overlay2/62af02f6cfbf9826fd9c63f79cddc8d78fdf1f92c991bccdafad390823d4bc6a/diff:/var/lib/docker/overlay2/59c508783c72e9098135ba0ac28e059150faf0d6ca94224496c59a9e1f54b98a/diff:/var/lib/docker/overlay2/6dc8609c85f2aea79ba6a6bc75305150756e8c948d437dd89fb9c3b682c910be/diff:/var/lib/docker/overlay2/8796acc235fd6bc244ce17c207b150cc45b650de36cceea1e8001d373d15b6be/diff:/var/lib/docker/overlay2/1621df36cbf8c80d843927b83d4657b8a31f5180ed4f9a81efee4512fcce322d/diff:/var/lib/docker/overlay2/4e06d97129bcb3ae3374cfcec4750228fabf18a2aa6e64a24579b235ffd1c455/diff:/var/lib/docker/overlay2/2bf6bdc48ed148b5935e9214a718bc217dbe27b6c0d55d1f2db0f75c818d9bd5/diff",
                "MergedDir": "/var/lib/docker/overlay2/e012e7212d6ab9e050b33b23c15379bf21c4d4358a936db095b00a646753516d/merged",
                "UpperDir": "/var/lib/docker/overlay2/e012e7212d6ab9e050b33b23c15379bf21c4d4358a936db095b00a646753516d/diff",
                "WorkDir": "/var/lib/docker/overlay2/e012e7212d6ab9e050b33b23c15379bf21c4d4358a936db095b00a646753516d/work" 
            },
            "Name": "overlay2"
        },
        "Mounts": [],
        "Config": {
            "Hostname": "c418eb9c0772",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "ExposedPorts": {
                "80/tcp": {}
            },
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": [
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "NGINX_VERSION=1.25.2",
                "NJS_VERSION=0.8.0",
                "PKG_RELEASE=1~bookworm"
            ],
            "Cmd": [
                "nginx",
                "-g",
                "daemon off;"
            ],
            "Image": "nginx",
            "Volumes": null,
            "WorkingDir": "",
            "Entrypoint": [
                "/docker-entrypoint.sh"
            ],
            "OnBuild": null,
            "Labels": {
                "maintainer": "NGINX Docker Maintainers \u003cdocker-maint@nginx.com\u003e"
            },
            "StopSignal": "SIGQUIT"
        },
        "NetworkSettings": {
            "Bridge": "",
            "SandboxID": "8637b094c7b3de516d011a52a9168d2a0a1890e989703e61fad335633fd79d6b",
            "HairpinMode": false,
            "LinkLocalIPv6Address": "",
            "LinkLocalIPv6PrefixLen": 0,
            "Ports": {
                "80/tcp": [
                    {
                        "HostIp": "0.0.0.0",
                        "HostPort": "8080"
                    }
                ]
            },
            "SandboxKey": "/var/run/docker/netns/8637b094c7b3",
            "SecondaryIPAddresses": null,
            "SecondaryIPv6Addresses": null,
            "EndpointID": "71ad608486f63dd3f07d8d02678f629dbdbac0bac8e81831baa1dc3cdd2832a8",
            "Gateway": "172.17.0.1",
            "GlobalIPv6Address": "",
            "GlobalIPv6PrefixLen": 0,
            "IPAddress": "172.17.0.2",
            "IPPrefixLen": 16,
            "IPv6Gateway": "",
            "MacAddress": "02:42:ac:11:00:02",
            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "7f80a70f0e312e407ca553ad5e228ce47ceca1708e14761e8b688f1e0847fc9a",
                    "EndpointID": "71ad608486f63dd3f07d8d02678f629dbdbac0bac8e81831baa1dc3cdd2832a8",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:11:00:02",
                    "DriverOpts": null
                }
            }
        }
    }
]
```

대략적으로 중요하게 볼 것들을 살펴보자. 물론 다 중요하다. 공부하면서 알아볼 것들을 좀 찾아본 것이다.

`"Path": "/docker-entrypoint.sh"` 로 최상위 디렉토리에서 sh 파일이 실행되었다는 것을 알 수 있다.

`CMD` 는 Dockerfile을 `CMD` 를 정의할 때 사용한다. `CMD [”nginx”,”-g”,”daemon off;”]`

```
"Cmd": [
    "nginx",
    "-g",
    "daemon off;"
]
```

`Args` 를 보면 어디서 본거 같지 않나?  [Dockerfile(3)](https://www.notion.so/4c11c5b9fa42490ebbfb739bc4539fb4?pvs=21) 에서 `ENTRYPOINT ["nginx","-g","daemon off;"]` 사용된 값들이다.

```
"Args": [
    "nginx",
    "-g",
    "daemon off;"
]
```

`PortBindings` 를 통해 호스트시스템서버의 포트와 nginx 80번 포트가 바인딩 되어있다는 것을 알 수 있다.

```
"PortBindings": {
    "80/tcp": [
        {
            "HostIp": "",
            "HostPort": "8080"
        }
    ]
}
```

`ExposedPorts` 는 Dockerfile을 사용할 때 `EXPOSE` 와 연결된다.

`EXPOSE`는 호스트 시스템 서버나 다른 컨테이너에서 접근할 수 있도록 포트를 노출하는 데 사용된다. 그래서 우린 80번 포트가 열려있다는 것을 알 수 있다. 즉, 알려주는 표시 정도로 알고 있다.

```
FROM nginx

EXPOSE 80
```

```
"ExposedPorts": {
    "80/tcp": {}
}
```

`"Gateway": "172.17.0.1"` 이 IP 주소는 컨테이너가 외부와 통신할 때 패킷이 게이트웨이를 통해 라우팅되는 지점을 가리킨다. 즉, 호스트 시스템 서버를 가르킨다고 이해하면 쉽다.

`"IPAddress": "172.17.0.2"` 를 통해 docker container끼리도 연결 할 수 있다. 내부적인 가상 서버의 IP 주소인 것이다.

![Untitled (3)](https://github.com/user-attachments/assets/1a5f18d6-86c9-4e36-8534-a55dbe0234e6)

더 자세히 알기 위해 터미널을 하나 더 열어서 아래 명령어를 적어보자.

```docker
docker exec -it nginx-detail bash
```

root에 접속 된 것을 확인 후 `ls` 를 입력 해보면 [docker-entrypoint.sh](http://docker-entrypoint.sh) 파일을 확인 할 수 있다.

![Untitled (4)](https://github.com/user-attachments/assets/7a9859ab-b2c5-4764-b981-1bfd826f5a16)

`cat` 을 이용해 sh 파일을 읽어보자.

```
cat docker-entrypoint.sh
```

스크립트를 통해 내부적으로 동작되는 명령어들을 볼 수 있다.

```
#!/bin/sh
# vim:sw=4:ts=4:et

set -e

entrypoint_log() {
    if [ -z "${NGINX_ENTRYPOINT_QUIET_LOGS:-}" ]; then
        echo "$@"
    fi
}

if [ "$1" = "nginx" ] || [ "$1" = "nginx-debug" ]; then
    if /usr/bin/find "/docker-entrypoint.d/" -mindepth 1 -maxdepth 1 -type f -print -quit 2>/dev/null | read v; then
        entrypoint_log "$0: /docker-entrypoint.d/ is not empty, will attempt to perform configuration"     

        entrypoint_log "$0: Looking for shell scripts in /docker-entrypoint.d/"
        find "/docker-entrypoint.d/" -follow -type f -print | sort -V | while read -r f; do
            case "$f" in
                *.envsh)
                    if [ -x "$f" ]; then
                        entrypoint_log "$0: Sourcing $f";
                        . "$f"
                    else
                        # warn on shell scripts without exec bit
                        entrypoint_log "$0: Ignoring $f, not executable";
                    fi
                    ;;
                *.sh)
                    if [ -x "$f" ]; then
                        entrypoint_log "$0: Launching $f";
                        "$f"
                    else
                        # warn on shell scripts without exec bit
                        entrypoint_log "$0: Ignoring $f, not executable";
                    fi
                    ;;
                *) entrypoint_log "$0: Ignoring $f";;
            esac
        done

        entrypoint_log "$0: Configuration complete; ready for start up"
    else
        entrypoint_log "$0: No files found in /docker-entrypoint.d/, skipping configuration"
    fi
fi

exec "$@"
```

`if /usr/bin/find "/docker-entrypoint.d/"` → 리눅스 환경 셸 스크립트 구문으로 **`find`** 명령을 사용하여 **`/docker-entrypoint.d/`** 디렉토리에서 파일 또는 디렉토리를 찾는 명령이다. **`find`** 명령은 파일 시스템을 검색하고 파일 또는 디렉토리를 찾는 데 사용된다.

`docker-enrtypoint.d`디렉토리 안에도 한번 찾아보자.

```
cd docker-enrtypoint.d
```

![Untitled (5)](https://github.com/user-attachments/assets/c163a89a-3025-4711-bac8-f14e24474ff4)

---

이렇게 찾아보았는데…. docker를 공부하기 전 리눅스 공부가 먼저 선행하는 게 좋을 것 같고,,, 셸 스크립트를 공부해야 docker를 좀 더 편하게 할 수 있을 것 같다고 많이 느끼게 되었다…
