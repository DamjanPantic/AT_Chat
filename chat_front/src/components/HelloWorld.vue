<template>
  <b-container>
    <b-row>
      <b-col>
        <b-form-input v-model="message" type="text" placeholder="Message Text.." />
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <b-button @click="sendMessage" class="mt-3" pill variant="primary">Send Message</b-button>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import axios from 'axios'

export default {
  name: "HelloWorld",
  data(){
    return {
      message: ""
    }
  },
  methods: {
    sendMessage(){
      axios.post("chat/post/" + this.message).then(response => {
        console.log(response.data);
        console.log('Sent message to the server.');
        this.message = "";
      }).catch(e => {
        console.log(e);
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
</style>
