<script setup lang="ts">
import { ref } from 'vue';
import { doSignInWithEmailAndPassword, doSignInWithGoogle } from '../firebase/auth';
import type { LoginResponse } from '../types/LoginResponse';
import ErrorMessage from '../components/form/ErrorMessage.vue';
import BasicInput from '../components/form/BasicInput.vue';
import ProviderLoginButton from '../components/form/ProviderLoginButton.vue';
import BasicButton from '../components/form/BasicButton.vue';
import Logo from '../components/Logo.vue';

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

</script>

<template>
         <main className="min-h-screen bg-form text-black flex justify-center p-4">
           
          <div className="bg-white p-8 flex flex-col gap-6 pt-12 rounded-md shadow-lg">
            
            <div className="flex flex-col gap-3">
            <h1 className="text-3xl font-bold text-primary-color">Log in to your account</h1>
            <h2 className="font-bold">Don't have an account? Sign Up</h2>
            </div>
            <div className="flex flex-col w-[21rem] gap-5">
                <ProviderLoginButton @click="onSubmitWithGoogle" url="/google.png">Google</ProviderLoginButton>
                <div className="flex items-center gap-3"><div className="bg-slate-700 h-[0.09rem] w-full"></div>
                <p className="min-w-max flex-grow">Or with email & password</p>
                <div className="bg-slate-700 h-[0.09rem] w-full"></div></div>
                <form @submit={onSubmit} className="flex flex-col gap-3">
                <ErrorMessage :error-message="response.errorMsg" v-if="response.status === 'ERROR'" />
                    <BasicInput
                    label="Email Address*"
                    type="email"
                    id="email-login-input" value={email} :model-value="email"/>
                    <BasicInput
                    toggleVisibility
                    label="Password*"
                    type="password"
                    id="password-login-input" value={password} :model-value="password"/>
                    <div className="flex justify-end"></div>
                    <BasicButton @click="onSubmit" :style="{ marginTop: '1rem' }" type="submit"> <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>Login</BasicButton>
                </form>
            </div>
          </div>

        </main>
</template>

<style scoped>

</style>