<template>
    <div class="container">
      <div>
        <label style="color: white">File
          <input type="file" id="file" ref="file" @change="handleFileUpload( $event )"/>
        </label>
        <button v-on:click="submitFile()">Submit</button>
      </div>
    </div>
  </template>

<style scoped>
.container {
  margin-block: 1rem;
  display: grid;
  place-items: center;
  width: 100%;
  div{
    max-width: 20rem;
    display: flex;
    gap: 0.5rem;
    flex-direction: column;
  }
}
</style>
  
  <script>
import { uploadImage } from '../http-api';
import { useImageStore } from '../store/imageStore';

    export default {
      /*
        Defines the data used by the component
      */
      data(){
        return {
          file: ''
        }
      },
  
      methods: {
        /*
          Submits the file to the server
        */
        async submitFile(){
          const imageStore = useImageStore();
          await uploadImage(this.file)
          imageStore.triggerReload()
        },
  
        /*
          Handles a change on the file upload
        */
        handleFileUpload(event){
            this.file = event.target.files[0];
        }
      }
      
    }
  </script>