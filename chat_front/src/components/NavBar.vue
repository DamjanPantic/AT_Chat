<template>
  <b-navbar toggleable="lg" type="dark" variant="primary">
    <router-link to="/" tag="b-navbar-brand">
      ChatApp
      <b-icon icon="chat"></b-icon>
    </router-link>
    <b-navbar-nav class="ml-auto">
      <b-nav-item v-if="this.$store.state.user" @click="logoutUser">
        Logout
        <b-icon icon="person-dash"></b-icon>
      </b-nav-item>
    </b-navbar-nav>
  </b-navbar>
</template>

<script>
import axios from "axios";

export default {
  methods: {
    logoutUser() {
      axios
        .delete("users/loggedIn/" + this.$store.state.user.username)
        .then(() => {
          this.$store.state.user = null;
          this.$router.push("/login");
        })
        .catch(e => {
          console.log(e);
        });
    }
  }
};
</script>

<style scoped>
</style>