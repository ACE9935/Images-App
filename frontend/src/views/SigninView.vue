<script setup lang="ts">
import { ref, watch } from 'vue';
import { doSignInWithEmailAndPassword, doSignInWithGoogle } from '../firebase/auth';
import type { LoginResponse } from '../types/LoginResponse';
import ErrorMessage from '../components/utility/ErrorMessage.vue';
import BasicInput from '../components/form/BasicInput.vue';
import ProviderLoginButton from '../components/form/ProviderLoginButton.vue';
import BasicActionButton from '../components/form/BasicActionButton.vue';
import AuthFomsNavigator from '../components/form/AuthFomsNavigator.vue';
import ResetPasswordModal from '../components/form/ResetPasswordModal.vue';
import { useToast } from "primevue/usetoast";
import { auth } from '../firebase/firebase';
import router from '../router';

const toast = useToast();

// Internal ref for the input value
const email = ref("");
const password = ref("");
const isLoading = ref(false);
const response = ref<LoginResponse>({ errorMsg: null, status: null })

const onSubmit = async (e: Event) => {
      e.preventDefault();
      
      if (!isLoading.value) {
          isLoading.value=true;
          const serverResponse = await doSignInWithEmailAndPassword(email.value, password.value);
          
          isLoading.value=false;
          response.value=serverResponse;
      }
  }

  const onSubmitWithGoogle = async (e: Event) => {
    e.preventDefault();
    
    if (!isLoading.value) {
        isLoading.value=true;
        const serverResponse = await doSignInWithGoogle();
        
        isLoading.value=false;
        response.value={errorMsg:serverResponse.errors as string,status:serverResponse.status}
    }
}

watch(
  () => response.value,
  (newResponse) => {
    if (newResponse.status === "OK") {
     toast.add({ severity: 'success', summary: 'Success', detail: `Nice to see you ${auth.currentUser?.displayName} !`, life: 3000 });
     router.push('/');
    }
  }
);

</script>

<template>
         <main class="min-h-screen bg-form text-black flex justify-center p-4">
           
          <div class="bg-white h-fit pb-12 p-8 flex flex-col gap-6 pt-12 rounded-md shadow-lg w-full max-w-[28rem]">
            
            <div class="flex flex-col gap-3">
            <h1 class="text-3xl font-bold text-primary-color text-center">Log in to your account</h1>
            <AuthFomsNavigator form="login" />
            </div>
            <div class="flex flex-col gap-5">
                <ProviderLoginButton @click="onSubmitWithGoogle" url="/imgs/google.png">Google</ProviderLoginButton>
                <div class="flex items-center gap-3"><div class="bg-slate-700 h-[0.09rem] w-full"></div>
                <p class="min-w-max flex-grow">Or with email & password</p>
                <div class="bg-slate-700 h-[0.09rem] w-full"></div></div>
                <form @submit={onSubmit} class="flex flex-col gap-3">
                <ErrorMessage :error-message="response.errorMsg" v-if="response.status === 'ERROR'" />
                    <BasicInput
                    label="Email Address*"
                    type="email"
                    id="email-login-input" v-model="email"/>
                    <BasicInput
                    toggleVisibility
                    label="Password*"
                    type="password"
                    id="password-login-input" v-model="password"/>
                    <div class="flex justify-end"><ResetPasswordModal/></div>

                    <BasicActionButton :is-loading="isLoading" :on-click="onSubmit" :style="{ marginTop: '1rem' }" type="submit">Login</BasicActionButton>
                    
                </form>
            </div>
          </div>

        </main>
</template>

<style scoped>

</style>