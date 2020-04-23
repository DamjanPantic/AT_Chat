<template>
  <div>
    <my-nav-bar />
    <b-container>
      <router-view class="mt-5" />
    </b-container>
  </div>
</template>

<script>
import NavBar from "./components/NavBar";

export default {
  components: {
    "my-nav-bar": NavBar
  }
};
</script>

<style lang="scss">
</style>
