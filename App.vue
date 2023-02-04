<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <HelloWorld msg="Welcome to Your Vue.js App"/>
    <input type="text" id="id"/>
    <input type="text" id="pwd"/>
    <button type="submit" @click="login()">로그인</button>
    <button id="logout" @click="close()">종료시키기</button>
</template>

<script>
import axios from 'axios';
import HelloWorld from './components/HelloWorld.vue'

export default {
  name: 'App',
  components: {
    HelloWorld
  },
  methods: {
    login() {
      const id = document.getElementById('id').value;
      const pwd = document.getElementById('pwd').value;
      const member = {
        id: id,
        pwd: pwd,
      }

      console.log(member);

      const eventSource = new EventSource(`http://localhost:8080/connect/${id}`);
      

      const logoutbtn = document.getElementById('logout');
      logoutbtn.onclick = () => {
        console.log('close');
        eventSource.close();
        
      }
      console.log(id);
      console.log(eventSource);
      
      eventSource.onmessage = event => {
        const data = JSON.parse(event.data);
        console.log("onmessage")
        console.log(data.message);
      };

      eventSource.addEventListener("complete", e => {
        console.log(e);
        console.log("complete");
        eventSource.close();
      })

        eventSource.addEventListener("data", e => {
            console.log(e);
        }, false);
        eventSource.addEventListener("sse", function (event) {
            console.log(event.data);
        
            // const data = JSON.parse(event.data);

            (async (data) => {
                // 브라우저 알림
                const showNotification = () => {
                    console.log("noti");
                    const notification = new Notification("alarm", {
                        url: "http://localhost:8081"
                    });

                    console.log(notification);
                    
                    setTimeout(() => {
                        notification.close();
                    }, 10 * 1000);
                    
                    notification.addEventListener('click', () => {
                        console.log("click");
                        window.open(data.url, '_blank');
                    });
                }

                // 브라우저 알림 허용 권한
                let granted = false;

                if (Notification.permission === 'granted') {
                    granted = true;
                } else if (Notification.permission !== 'denied') {
                    let permission = await Notification.requestPermission();
                    granted = permission === 'granted';
                }

                // 알림 보여주기
                if (granted) {
                    showNotification();
                }
            })();
        });
    },
    close() {
        const id = document.getElementById('id').value;
        const pwd = document.getElementById('pwd').value;

        const member = {
            id: id,
            pwd: pwd,
        }
        console.log(member);

        axios.get(`http://localhost:8080/complete/${id}`)
        .then((e) => console.log(e.data))
        .catch((err) => console.log(err));

    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
