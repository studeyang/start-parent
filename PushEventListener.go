package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
	"os/exec"
	"time"
)

func main() {
	//第一个参数是接口名，第二个参数 http handle func
	http.HandleFunc("/listener", sync)
	//服务器要监听的主机地址和端口号
	http.ListenAndServe("127.0.0.1:11118", nil)
}

// http handle func
func sync(rw http.ResponseWriter, req *http.Request) {
	execCommand("git pull")
	execCommand("cd /home/studeyang/start-parent")
	execCommand("sh build.sh")

	fmt.Println("build success at: " + time.Now().String())

	fmt.Fprint(rw, "build success.")
}

func execCommand(strCommand string) string {
	cmd := exec.Command("/bin/bash", "-c", strCommand)

	stdout, _ := cmd.StdoutPipe()
	if err := cmd.Start(); err != nil {
		fmt.Println("Execute failed when Start:" + err.Error())
		return ""
	}

	out_bytes, _ := ioutil.ReadAll(stdout)
	stdout.Close()

	if err := cmd.Wait(); err != nil {
		fmt.Println("Execute failed when Wait:" + err.Error())
		return ""
	}

	return string(out_bytes)
}
