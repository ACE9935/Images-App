<script setup lang="ts">
import { ref, watch } from 'vue';
import ErrorMessage from '../components/utility/ErrorMessage.vue';
import BasicInput from '../components/form/BasicInput.vue';
import BasicActionButton from '../components/form/BasicActionButton.vue';
import AuthFomsNavigator from '../components/form/AuthFomsNavigator.vue';
import { useToast } from "primevue/usetoast";
import router from '../router';
import { useRoute } from 'vue-router';
import type { PwdResetFormResponse } from '../types/PwdResetFormResponse';
import { updatePassword } from '../firebase/auth';

const toast = useToast();

// Internal ref for the input value
const data=ref({ pwd: "", rePwd: "" })
const isLoading = ref(false);
const response = ref<PwdResetFormResponse>({ errors: null, status: null })

const route = useRoute()

const token = route.query['token'] as string;
const userId = route.query['id'] as string;

const onSubmit = async (e: Event) => {
        e.preventDefault();
        
        if (!isLoading.value) {
            isLoading.value=true;
            const serverResponse = await updatePassword(data.value.pwd, data.value.rePwd, token, userId);
            
            isLoading.value=false;
            response.value=serverResponse;
        }
    }

watch(
  () => response.value,
  (newResponse) => {
    if (newResponse.status === "OK") {
     toast.add({ severity: 'success', summary: 'Success', detail: 'Your password has been reset !', life: 3000 });
     router.push('/login');
    }
  }
);

</script>

<template>
         <main className="min-h-screen bg-form text-black flex justify-center p-4">
           
          <div className="bg-white h-fit pb-12 p-8 flex flex-col gap-6 pt-12 rounded-md shadow-lg w-full max-w-[28rem]">
            
            <div className="flex flex-col gap-3">
            <h1 className="text-3xl font-bold text-primary-color text-center">Reset your account's password</h1>
            <AuthFomsNavigator form="none" />
            </div>
            <div className="flex flex-col gap-5">
  
                <form @submit={onSubmit} className="flex flex-col gap-3">
                <ErrorMessage error-message="An error occurred" v-if="response.status === 'ERROR'" />
                    <BasicInput
                    label="New Password*"
                    type="password"
                    :error="!!response.errors?.pwd.length"
                    :helperText="response.errors?.pwd"
                    id="email-login-input" v-model="data.pwd"/>
                    <BasicInput
                    toggleVisibility
                    label="Confirm Password*"
                    type="password"
                    :error="!!response.errors?.rePwd.length"
                    :helperText="response.errors?.rePwd"
                    id="password-login-input" v-model="data.rePwd"/>
                    <BasicActionButton :is-loading="isLoading" :on-click="onSubmit" :style="{ marginTop: '1rem' }" type="submit">Reset password</BasicActionButton>
                </form>
            </div>
          </div>

        </main>
</template>

<style scoped>

</style>