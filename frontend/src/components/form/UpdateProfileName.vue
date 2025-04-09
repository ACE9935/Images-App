<template>

<Dialog v-model:visible="visibleDialog" modal header="Edit Name" class="w-min">
     <div class="text-black flex flex-col gap-6 p-8 pt-2">
      <div class="flex self-end relative">
      </div>
  
      <form @submit="handleSubmit" class="flex flex-col gap-3">
        <BasicInput
        maxLength="24"
        :style="{ width: '20rem' }"
        placeholder="Enter a name..."
        v-model="newName"
        id="name-reset-input"
        label="New Name*" 
          />
          <BasicActionButton :is-loading="isLoading" :on-click="handleSubmit" type="submit">Confirm</BasicActionButton>
        
      </form>
    </div>
    </Dialog>

    <BasicButton class="w-full mt-1" variant="gray" :disabled="isLoading" @click="visibleDialog=true">
        <i v-if="isLoading" class="pi pi-spinner pi-spin" style="font-size: 1rem"></i>
        <i v-else class="pi pi-pencil" style="font-size: 1rem"></i>
        Edit Name
    </BasicButton>

</template>

<script setup lang="ts">
import { computed, ref, inject } from 'vue';
import BasicButton from '../form/BasicButton.vue';
import type { UserContext } from '../../types/UserContext';
import { useToast, Dialog } from 'primevue';
import BasicActionButton from './BasicActionButton.vue';
import BasicInput from './BasicInput.vue';
import { updateUsername } from '../../firebase/updateUserName';

const userContext = inject<UserContext>('userContext');
const visibleDialog = ref(false);
const isLoading = ref(false);
const newName = ref<string>("");

const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;
const toast = useToast();

const handleSubmit = async (e:Event)=>{
e.preventDefault();
if(!user.value || isLoading.value || !newName?.value.length) return;
try{
    isLoading.value = true;
    await updateUsername(user.value.id, newName.value);
    await refetchUser?.()
    console.log("Display name edited successfully!");
    toast.add({ severity: 'success', summary: 'Success', detail: 'Display name edited successfully!', life: 3000 });
    visibleDialog.value = false;
}
catch(e){
    console.log(e)
}
finally{
    isLoading.value = false;
}

}

</script>
