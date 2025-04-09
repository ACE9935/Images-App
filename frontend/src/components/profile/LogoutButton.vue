<template>
   
    <BasicButton @click="confirm1()" v-bind="$attrs">
        <i class="pi pi-sign-out" style="font-size: 1rem"></i>
        Logout
    </BasicButton>

</template>

<script setup>
import { useConfirm } from "primevue/useconfirm";
import { useToast } from "primevue/usetoast";
import BasicButton from "../form/BasicButton.vue";
import { signOut } from "firebase/auth";
import { auth } from "../../firebase/firebase";
import router from "../../router";

const confirm = useConfirm();
const toast = useToast();

const confirm1 = () => {
    confirm.require({
        message: 'Are you sure you want to proceed?',
        header: 'Logout',
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
            label: 'Cancel',
            severity: 'secondary',
            outlined: true
        },
        acceptProps: {
            label: 'Logout'
        },
        accept: async () => {
            auth.signOut()
            router.push("/")
        }
    });
};

</script>
