<template>

<Dialog v-model:visible="visibleDialog" modal header="Add Image to Album" class="w-min">
  <div class="text-black flex flex-col gap-6">

    <div v-if="user?.albums.length!=0" class="flex flex-col gap-2">
      <label class="font-semibold mb-1">Your Albums:</label>
      <div class="flex flex-wrap gap-2 max-h-[6.5rem] overflow-y-auto">
        <BookmarkToExistingAlbum
          v-for="album in user?.albums"
          :album="album"
          :img-id="props.imgId"
          :album-id="album.id"
          :key="album.id"
        />
      </div>
    </div>
    <StyledHeading v-else class="!text-2xl text-center">No albums were found.</StyledHeading>
    
    <div class="flex items-center gap-3">
          <div class="bg-slate-700 h-[0.09rem] w-full"></div>
          <p class="min-w-max flex-grow">Or create a new album</p>
          <div class="bg-slate-700 h-[0.09rem] w-full"></div>
        </div>

    <form @submit="handleSubmit" class="flex flex-col gap-2">
      <BasicInput
        maxLength="24"
        :style="{ width: '20rem' }"
        v-model="newAlbumName"
        id="name-reset-input"
        label="Album Title*"
      />
      <BasicActionButton :on-click="handleSubmit" :is-loading="isLoading" icon="pi-plus" type="submit">
        Create Album
      </BasicActionButton>
    </form>

  </div>
</Dialog>
<BasicActionButton class="py-3" :is-loading="isLoading" :on-click="handleClick" icon="pi-bookmark" />
</template>
    
<script setup lang="ts">
import BasicActionButton from './form/BasicActionButton.vue';
import { ref, inject, computed, watch} from 'vue';
import type { UserContext } from '../types/UserContext';
import { useToast, Dialog } from 'primevue';
import BasicInput from './form/BasicInput.vue';
import addNewAlbumToUser from '../firebase/addAlbumToUser';
import StyledHeading from './StyledHeading.vue';
import BookmarkToExistingAlbum from './BookmarkToExistingAlbum.vue';
import router from '../router';

const props=defineProps<{
    imgId:string,
    forceClose?:number,
}>()

const userContext = inject<UserContext>('userContext');
const isLoading = ref(false);
const visibleDialog = ref(false);
const newAlbumName = ref<string>("");

const user = computed(() => userContext?.user.value);
const refetchUser = userContext?.refreshUser;
const toast = useToast();

const handleClick = () => {
  if (!user.value) {
    router.push("/login");
    return;
  }
  visibleDialog.value = true;
}

const handleSubmit = async (e:Event)=>{
e.preventDefault();
if(!user.value || isLoading.value || !newAlbumName?.value.length) return;
try{
    isLoading.value = true;
    await addNewAlbumToUser(user.value.id, newAlbumName.value, props.imgId);
    await refetchUser?.()
    console.log("Album created successfully!");
    toast.add({ severity: 'success', summary: 'Success', detail: 'Album created successfully!', life: 3000 });
}
catch(e){
    console.log(e)
}
finally{
    isLoading.value = false;
}

}

watch(() => props.forceClose, (newValue, oldValue) => {
    if (newValue!== oldValue) {
        visibleDialog.value = false;
    }
});
    
</script>
    