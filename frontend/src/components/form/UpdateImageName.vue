<template>

    <Dialog v-model:visible="visibleDialog" modal header="Edit Image title" class="w-min">
         <div class="text-black flex flex-col gap-6 p-8 pt-2">
          <div class="flex self-end relative">
          </div>
      
          <form @submit="handleSubmit" class="flex flex-col gap-3">
            <BasicInput
            maxLength="24"
            :style="{ width: '20rem' }"
            placeholder="Enter a title..."
            v-model="newName"
            id="name-reset-input"
            label="New Title*" 
              />
              <BasicActionButton :is-loading="isLoading" :on-click="handleSubmit" type="submit">Confirm</BasicActionButton>
            
          </form>
        </div>
        </Dialog>
    
        <Button v-bind="$attrs" size="small" v-if="isAuthor" @click="visibleDialog=true" icon="pi pi-pencil" variant="outlined" rounded aria-label="Edit Image name" />
    
    </template>
    
<script setup lang="ts">
    import { computed, ref, inject } from 'vue';
    import Button from 'primevue/button';
    import type { UserContext } from '../../types/UserContext';
    import { useToast, Dialog } from 'primevue';
    import BasicActionButton from './BasicActionButton.vue';
    import BasicInput from './BasicInput.vue';
    import { updateImageTitle } from '../../http-api';
    
    const props = defineProps<{
        imgId:string;
        refetchImage: () => Promise<void>;
    }>();

    const userContext = inject<UserContext>('userContext');
    const visibleDialog = ref(false);
    const isLoading = ref(false);
    const newName = ref<string>("");
    
    const user = computed(() => userContext?.user.value);
    const toast = useToast();

    const isAuthor = computed(() => 
     user.value?.uploadedImages?.map(Number).includes(Number(props.imgId)) ?? false
    );
    
    const handleSubmit = async (e:Event)=>{

    e.preventDefault();
    if(!user.value || isLoading.value || !newName?.value.length) return;
    if(!isAuthor.value) {
        console.log("You are not the author of this image!");
        return;
    }
    try{
        isLoading.value = true;
        await updateImageTitle(props.imgId, newName.value);
        await props.refetchImage()
        console.log("Image title edited successfully!");
        toast.add({ severity: 'success', summary: 'Success', detail: 'Image title edited successfully!', life: 3000 });
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
    