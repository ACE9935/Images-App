<template>
   
    <BasicButton :disabled="isLoading" @click="confirm1()" v-bind="$attrs">
        <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
        <i v-else class="pi pi-trash" style="font-size: 1rem"></i>
        Delete account
    </BasicButton>

</template>

<script setup>
import { useConfirm } from "primevue/useconfirm";
import { useToast } from "primevue/usetoast";
import BasicButton from "../form/BasicButton.vue";
import { ref } from "vue";
import { auth } from "../../firebase/firebase";
import router from "../../router";
import { deleteUserAccount } from "../../firebase/deleteUserAccount";

const confirm = useConfirm();
const toast = useToast();
const isLoading=ref(false)

const confirm1 = () => {
    confirm.require({
        message: 'Are you sure you want to proceed?',
        header: 'Delete Account',
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
            label: 'Cancel',
            severity: 'secondary',
            outlined: true
        },
        acceptProps: {
            label: 'Delete'
        },
        accept: async () => {
            isLoading.value=true
            await deleteUserAccount(auth.currentUser.uid)
            .then(()=>isLoading.value=false)
            .then(()=> toast.add({ severity: 'success', summary: 'Success', detail: 'Account deleted successfully !', life: 3000 }))
            .then(()=>router.push("/"))
        }
    });
};

</script>
