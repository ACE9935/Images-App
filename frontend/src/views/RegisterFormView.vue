<script setup lang="ts">
import { ref, watch } from 'vue';
import { doCreateUserWithEmailAndPassword, doSignInWithGoogle } from '../firebase/auth';
import BasicInput from '../components/form/BasicInput.vue';
import ProviderLoginButton from '../components/form/ProviderLoginButton.vue';
import BasicActionButton from '../components/form/BasicActionButton.vue';
import AuthFomsNavigator from '../components/form/AuthFomsNavigator.vue';
import { auth } from '../firebase/firebase';
import Dialog from 'primevue/dialog';
import SuccessMessage from '../components/utility/SuccessMessage.vue';
import AppAlert from '../components/utility/AppAlert.vue';
import ErrorMessage from '../components/utility/ErrorMessage.vue';
import type { RawUser } from '../types/RawUser';
import type { SignUpResponse } from '../types/SignUpResponse';
import BasicButton from '../components/form/BasicButton.vue';
import router from '../router';

// Internal ref for the input value
const initialUser = ref<RawUser>({
  userName: "",
  email: "",
  pwd: "",
  rePwd: "",
  acceptPlcs: false,
});
const visibleDialog = ref(false);
const isLoading = ref(false);
const response = ref<SignUpResponse>({ errors: null, status: null });
const errors = ref<any>(response.value.errors);

const onSubmit = async (e: Event) => {
  e.preventDefault();

  if (!isLoading.value) {
    isLoading.value = true;
    const serverResponse = await doCreateUserWithEmailAndPassword({ ...initialUser.value });

    isLoading.value = false;
    response.value = serverResponse;
  }
};

const handleGoogleSignIn = async () => {
  const result = await doSignInWithGoogle();
  response.value = result;
  router.push('/');
};

watch(
  () => response.value,
  (newResponse) => {
    errors.value = newResponse.errors;
    if (newResponse.status === "OK" && newResponse.method === "credentials") {
      visibleDialog.value = true;
    }
  }
);
</script>

<template>
   
  <main class=" bg-form text-black flex justify-center p-4">
    <Dialog v-model:visible="visibleDialog" modal header="Verify your email" :style="{ width: '25rem' }">
     <div class="text-black flex flex-col gap-6 p-8 pt-2">
      <div class="flex self-end relative">
      </div>
      <h1 class="text-center text-2xl">Check your inbox.</h1>
      <p class="text-center">
        Click the link we sent to {{ auth.currentUser?.email }} to complete your account setup.
      </p>
        <BasicButton variant="neutral" @click="visibleDialog = false">OK</BasicButton>
    </div>
    </Dialog>
    <div class="bg-white h-fit pb-12 p-8 flex flex-col gap-6 pt-12 rounded-md shadow-lg w-full max-w-[28rem]">
      <div class="flex flex-col gap-3">
        <h1 class="text-3xl font-bold text-primary-color text-center">Register an account</h1>
        <AuthFomsNavigator form="register" />
      </div>
      <div class="flex flex-col gap-5">
        <ProviderLoginButton
          @click="handleGoogleSignIn"
          :url="'/imgs/google.png'"
        >
          Sign up with Google
        </ProviderLoginButton>
        <div class="flex items-center gap-3">
          <div class="bg-slate-700 h-[0.09rem] w-full"></div>
          <p class="min-w-max flex-grow">Or with email & password</p>
          <div class="bg-slate-700 h-[0.09rem] w-full"></div>
        </div>
        <form @submit="onSubmit" class="flex flex-col gap-3">
          <ErrorMessage
            error-message="An error occurred"
            v-if="response.status === 'ERROR'"
            severity="error"
          />
          <SuccessMessage
            v-if="response.status === 'OK' && response.method === 'google'"
            success-message="Your registration was successful! You can now access your account."
          />
          <AppAlert
            icon="pi-info-circle"
            v-else-if="response.status === 'OK' && response.method === 'credentials'"
          >
            To complete your registration, please open the link sent to your email address.
          </AppAlert>
          <template v-else></template>

          <BasicInput
            label="Name*"
            :helperText="errors?.error.userName || null"
            :error="!!errors?.error.userName"
            placeholder="John Doe"
            type="name"
            id="name-register-input"
            v-model="initialUser.userName"
          />

          <BasicInput
            label="Email Address*"
            :helperText="errors?.error.email || null"
            :error="!!errors?.error.email"
            type="email"
            id="email-register-input"
            v-model="initialUser.email"
          />

          <BasicInput
            label="Password*"
            :helperText="errors?.error.pwd || null"
            :error="!!errors?.error.pwd"
            type="password"
            id="password-register-input"
            v-model="initialUser.pwd"
          />

          <BasicInput
            label="Confirm Password*"
            :helperText="errors?.error.rePwd || null"
            :error="!!errors?.error.rePwd"
            type="password"
            id="repassword-register-input"
            v-model="initialUser.rePwd"
          />
        <div><span v-if="errors?.error.acceptPlcs" class="text-red-500 text-sm">{{errors?.error.acceptPlcs}}</span>
          <div class="flex gap-2">
            <input
              type="checkbox"
              id="accept-plcs-register-input"
              v-model="initialUser.acceptPlcs"
              class="w-[1.5rem] cursor-pointer"
            />
            <label for="accept-plcs-register-input">
              <p>
                I agree with the
                <a
                  target="_blank"
                  href="/"
                  class="text-primary-blue underline"
                >
                  privacy policy and terms
                </a>
                of this website
              </p>
            </label>
          </div>
        </div>

        <BasicActionButton :is-loading="isLoading" :on-click="onSubmit" :style="{ marginTop: '1rem' }" type="submit">Signup</BasicActionButton>

        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>
</style>