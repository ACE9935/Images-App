<script setup lang="ts">
import { ref, watch } from 'vue';
import BasicActionButton from './BasicActionButton.vue';
import BasicInput from './BasicInput.vue';
import Dialog from 'primevue/dialog';
import { resetPassword } from '../../firebase/auth';
import type { PwdResetResponse } from '../../types/PwdResetResponse';
import { useToast } from "primevue/usetoast";

const toast = useToast();

// Internal ref for the input value
const email = ref<string>("");

const visibleDialog = ref(false);
const isLoading = ref(false);
const response = ref<PwdResetResponse>({ errorMsg: null, status: null });

const onSubmit = async (e: Event) => {
        e.preventDefault();
        e.stopPropagation();
        if (!isLoading.value) {
            isLoading.value = true;
            const serverResponse = await resetPassword(email.value)
                .finally(() => {
                    isLoading.value = false;
                });
            response.value=serverResponse;
        }
    }

watch(
  () => response.value,
  (newResponse) => {
    if (newResponse.status === "OK") {
     visibleDialog.value = false;
     toast.add({ severity: 'success', summary: 'Success', detail: 'A password reset link has been sent to your email.', life: 3000 });
    }
  }
);
</script>

<template>
    <Dialog v-model:visible="visibleDialog" modal header="Password Reset" class="w-min">
     <div class="text-black flex flex-col gap-6 p-8 pt-2">
      <div class="flex self-end relative">
      </div>
      <p class="text-center">
        Enter your email address below to receive the password reset link.
      </p>
      <form @submit="onSubmit" class="flex flex-col gap-3">
        <BasicInput
        :error="response.status === 'ERROR'" 
        :style="{ width: '20rem' }"
        :helperText="response.errorMsg"
        v-model="email"
        id="email-reset-input"
        label="Email*" 
          />
          <BasicActionButton :is-loading="isLoading" :on-click="onSubmit" type="submit">Send Reset Link</BasicActionButton>
        
      </form>
    </div>
    </Dialog>
    <span @click="visibleDialog=true" className="text-sm font-bold underline text-blue-500 cursor-pointer">Forgot password?</span>
</template>

<style scoped>
</style>